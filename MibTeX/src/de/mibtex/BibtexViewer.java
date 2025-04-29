/* MibTeX - Minimalistic tool to manage your references with BibTeX
 * 
 * Distributed under BSD 3-Clause License, available at Github
 * 
 * https://github.com/tthuem/MibTeX
 */
package de.mibtex;

import de.mibtex.citationservice.CitationService;
import de.mibtex.export.*;
import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class to export a given BibTeX file to another format
 *
 * @author Thomas Thuem, Christopher Sontag, Paul Bittner
 */
public class BibtexViewer {

    public static String BIBTEX_DIR = "";

    public static String MAIN_DIR = "";

    public static String OUTPUT_DIR = "";

    public static String COMMENTS_DIR_REL = "";

    public static String PDF_DIR_REL = "";

    public static String PREPRINTS_DIR = "";

    public static String COMMENTS_DIR = "";

    public static String PDF_DIR = "";

    public static List<String> TAGS = new ArrayList<String>();

    public static List<String> FILTERTAGS = new ArrayList<String>();

    private static boolean cleanOutputDir;

    private static boolean citationServiceActive;

    private static String format = "HTML";

    public static String CITATION_DIR;

    /**
     * @param args array containing path to ini file
     */
    public static void main(String[] args) {
        String configurationFile;
        if (args.length == 0)
            configurationFile = "options.ini";
        else {
            configurationFile = args[0];
        }
        File iniFile = new File(configurationFile);
        if (iniFile.exists()) {
            Ini ini = null;
            if (System.getProperty("os.name").contains("Windows")) {
                try {
                    ini = new Wini(iniFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    ini = new Ini(iniFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ini != null) {
                BIBTEX_DIR = ini.get("options", "bibtex-dir");
                MAIN_DIR = ini.get("options", "main-dir");
                OUTPUT_DIR = MAIN_DIR + ini.get("options", "out-dir-rel");
                PDF_DIR = MAIN_DIR + ini.get("options", "pdf-dir");
                PDF_DIR_REL = ini.get("options", "pdf-dir-rel");
                COMMENTS_DIR = MAIN_DIR + ini.get("options", "comment-dir");
                PREPRINTS_DIR = ini.get("options", "preprints-dir");
                COMMENTS_DIR_REL = ini.get("options", "comment-dir-rel");
                try {
                	String[] tagArray = ini.get("options", "tags").split(",");
                	TAGS.addAll(Arrays.asList(tagArray));
                } catch (Exception e) {}
                try {
	                String[] filterTagArray = ini.get("options", "filter-tags").split(",");
	                FILTERTAGS.addAll(Arrays.asList(filterTagArray));
                } catch (Exception e) {}
                try {
                	cleanOutputDir = ini.get("options", "clean", Boolean.class);
                } catch (Exception e) {}
                try {
                	citationServiceActive = ini.get("options", "citation-service", Boolean.class);
                } catch (Exception e) {}
                String citationDir = ini.get("options", "citation-dir");
                if (citationDir == null || citationDir.isEmpty()) {
                    CITATION_DIR = BIBTEX_DIR;
                } else {
                    CITATION_DIR = citationDir;
                }
                format = ini.get("options", "out-format");
            } else {
                System.out.println("Ini file reader is null!");
                System.exit(0);
            }
        } else {
            System.out.println("Options file not found under: " + iniFile.getName());
        }

        try {
            if (citationServiceActive && !"Citations".equalsIgnoreCase(format)) {
                new BibtexViewer("Citations");
            }
            else if (format != null) {
                new BibtexViewer(format);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BibtexViewer(String format) throws Exception {
        Export exporter = null;
        switch (format.toUpperCase()) {
            case "CSV":
                exporter = new ExportCSV(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "JSON":
                exporter = new ExportJSON(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "CITATIONS":
                exporter = new ExportCitations(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "CONFLICTS":
                exporter = new ExportConflicts(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "CLASSIFICATION":
                exporter = new ExportClassification(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "SAMPLING":
                exporter = new ExportSampling(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "SAMPLING_LATEX":
                exporter = new ExportSamplingLatex(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "HTML_NEW":
                exporter = new ExportNewHTML(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "FIND_PDFS":
                exporter = new ExportFindPDFs(BibtexViewer.BIBTEX_DIR, "literature.bib");
                break;
            case "TYPO3":
            	exporter = new ExportTypo3Bibtex(BibtexViewer.BIBTEX_DIR, "literature.bib");
            	break;
            case "HTML":
            default:
                exporter = new ExportHTML(BibtexViewer.BIBTEX_DIR, "literature.bib");
        }
        if (cleanOutputDir) {
            Export.cleanOutputFolder();
        }
        exporter.writeDocument();
        Export.renameFiles(false);
        Export.renameFiles(true);
        if (citationServiceActive) {
            CitationService.main(new String[] {BibtexViewer.CITATION_DIR});
        }
    }
}
