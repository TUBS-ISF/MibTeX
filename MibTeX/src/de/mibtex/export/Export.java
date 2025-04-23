/* MibTeX - Minimalistic tool to manage your references with BibTeX
 * 
 * Distributed under BSD 3-Clause License, available at Github
 * 
 * https://github.com/tthuem/MibTeX
 */
package de.mibtex.export;

import de.mibtex.*;
import de.mibtex.citationservice.CitationEntry;
import org.jbibtex.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

/**
 * A abstract class that implements often used methods for the exporters
 *
 * @author Thomas Thuem
 */
public abstract class Export {

    protected static LinkedHashMap<String, BibtexEntry> entries;

    protected static List<String> authors;

    protected static List<String> titles;

    protected static List<Integer> years;

    protected static List<String> venues;

    protected static List<String> tags;

    public Export(String path, String file) throws Exception {
        Reader reader = null;
        try {
            reader = new FileReader(FileUtils.concat(path, file));
            BibTeXParser parser = new BibTeXParser() {
                @Override
                public void checkStringResolution(Key key, BibTeXString string) {
                }

                @Override
                public void checkCrossReferenceResolution(Key key,
                                                          BibTeXEntry entry) {
                }
            };
            BibTeXDatabase database = parser.parse(reader);
            extractEntries(database);
        } catch (IOException e) {
            System.out.println("BibTeXParser has an IOExeption");
            System.exit(0);
        } catch (ParseException e) {
            System.out.println("BibTeX-File cannot be parsed");
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            reader.close();
        }
        readAuthors();
        readTitles();
        readYears();
        readVenues();
        readTags();
    }

    private static void extractEntries(BibTeXDatabase database) {
        entries = new LinkedHashMap<String, BibtexEntry>();
        for (BibTeXObject object : database.getObjects()) {
            if (object instanceof BibTeXEntry) {
                BibtexEntry bibtexEntry = new BibtexEntry((BibTeXEntry) object);
                if (!entries.containsKey(bibtexEntry.key)) {
                    entries.put(bibtexEntry.key, bibtexEntry);
                } else {
                    System.out.println("Found duplicate key: "
                            + bibtexEntry.key);
                }
            }
        }
        readCitations();
    }

    private static void readCitations() {
        List<CitationEntry> citationsEntries = new ArrayList<CitationEntry>();
        File fileHandle = new File(BibtexViewer.CITATION_DIR, "citations.csv");
        if (fileHandle.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(
                    fileHandle))) {
                for (String line; (line = br.readLine()) != null; ) {
                    citationsEntries.add(CitationEntry.getFromCSV(line));
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for (CitationEntry citationEntry : citationsEntries) {
                if (entries.containsKey(citationEntry.getKey())) {
                    BibtexEntry bibtexEntry = entries.get(citationEntry
                            .getKey());
                    bibtexEntry.citations = citationEntry.getCitations();
                    bibtexEntry.lastUpdate = citationEntry.getLastUpdate();
                }
            }
        }

    }

    private static void readAuthors() {
        authors = new ArrayList<String>();
        for (BibtexEntry entry : entries.values())
            for (String author : entry.authorList)
                if (!authors.contains(author))
                    authors.add(author);
        Collections.sort(authors);
    }

    private static void readTitles() {
        titles = new ArrayList<String>();
        for (BibtexEntry entry : entries.values())
            titles.add(entry.title);
        Collections.sort(titles);
    }

    private static void readYears() {
        years = new ArrayList<Integer>();
        for (BibtexEntry entry : entries.values())
            if (!years.contains(entry.year))
                years.add(entry.year);
        Collections.sort(years);
    }

    private static void readVenues() {
        venues = new ArrayList<String>();
        for (BibtexEntry entry : entries.values()) {
        	// TODO better solution would be to do these replacements with MYshort
        	if ("GPCE13".equals(entry.venue))
        		entry.venue = "GPCE";
        	if ("VAMOS20".equals(entry.venue))
        		entry.venue = "VAMOS";
            if (!venues.contains(entry.venue))
                venues.add(entry.venue);
        }
        Collections.sort(venues);
    }

    private static void readTags() {
        tags = new ArrayList<>();
        for (BibtexEntry entry : entries.values())
            for (List<String> tagList : entry.tagList.values()) {
                for (String tag : tagList)
                    if (!tags.contains(tag)) {
                        tags.add(tag);
                    }
            }
        Collections.sort(tags);
    }

    public static void renameFiles(boolean comments) {
    	File folder = new File(comments ? BibtexViewer.COMMENTS_DIR : BibtexViewer.PDF_DIR);
    	if (!folder.exists())
    		return;
    	System.out.println("Checking the following folder for PDFs: " + folder.getAbsolutePath());
    	
        List<BibtexEntry> missing = new ArrayList<BibtexEntry>();
        List<File> available = findAvailablePDFs(folder);
        for (BibtexEntry entry : entries.values()) {
            File file = comments ? entry.getCommentsPath() : entry.getPDFPath();
            if (file.exists()) {
                if (!available.remove(file))
                    System.err.println("File comparison failed: " + file);
            } else {
                if (!"misc book".contains(entry.entry.getType().getValue()))
                    missing.add(entry);
            }
        }
        System.out.println("Correct = " + (entries.size() - missing.size())
                + ", Available = " + available.size() + ", Missing = "
                + missing.size());
        for (File file : available) {
            System.out.println("Available: " + file.getName());
        }
        Scanner answer = new Scanner(System.in);
        while (!available.isEmpty()) {
            int minDistance = Integer.MAX_VALUE;
            BibtexEntry missingEntry = null;
            File availableFile = null;
        	File newName = null;
            for (BibtexEntry entry : missing) {
                for (File file : available) {
                	File currentName = comments ? entry.getCommentsPath() : entry.getPDFPath();
                    int distance = Levenshtein.getDistance(file.getName(), currentName.getName());
                    if (distance < minDistance) {
                        minDistance = distance;
                        missingEntry = entry;
                        availableFile = file;
                        newName = currentName;
                    }
                }
            }
            // stop if names are too different from each other
            if (minDistance > availableFile.getName().length() * 0.7)
                break;
            if (availableFile != null) {
                System.out.println();
                System.out.println("Available: " + availableFile.getName());
                System.out.println("Missing: "
                        + newName.getName());
                System.out.println("Key: "
                        + missingEntry.entry.getKey().getValue());
                System.out.println("Distance: " + minDistance);
                System.out.println("Remaining: " + missing.size());
                if (answer.next().equals("y")) {
                    if (availableFile.renameTo(newName))
                        available.remove(availableFile);
                    else
                        System.err.println("Renaming from \""
                                + availableFile.getAbsolutePath() + "\" to \""
                                + newName
                                + "\" did not succeed!");
                }
                missing.remove(missingEntry);
            }
        }
        answer.close();
    }

    public static List<File> findAvailablePDFs(File directory) {
    	List<File> pdfs = new ArrayList<File>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                pdfs.addAll(findAvailablePDFs(file));
            }
            else if (file.getName().endsWith(".pdf")) {
                pdfs.add(file);
            }
        }
        return pdfs;
    }

    public static void cleanOutputFolder() {
        File[] files = new File(BibtexViewer.OUTPUT_DIR).listFiles();
        if (files != null)
            for (File file : files)
                file.delete();
    }

    protected static long countEntries(BibtexFilter filter) {
        long number = 0;
        for (BibtexEntry entry : entries.values())
            if (filter.include(entry))
                number++;
        return number;
    }
    
    protected static String readFromFile(String dir, File filename) {
    	return readFromFile(dir, filename.toString());
    }
    
    protected static String readFromFile(String dir, String filename) {
    	return readFromFile(new File(dir, filename));
    }

    protected static String readFromFile(File path) {
        try {
            InputStream in = new FileInputStream(path);
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            in.close();
            return out.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Not Found " + path);
        } catch (IOException e) {
            System.out.println("IOException for " + path);
        }
        return "";
    }
    
    protected static BufferedReader readFromFile(File path, Charset encoding) {
		try {
			FileInputStream fi = new FileInputStream(path);
	    	InputStreamReader isr = new InputStreamReader(fi, encoding);
	    	return new BufferedReader(isr);
		} catch (FileNotFoundException e) {
            System.out.println("Not Found " + path);
			e.printStackTrace();
		}
		return null;
    }

    protected static void writeToFile(String path, String filename, String content) {
        writeToFile(new File(path + filename), content);
    }
    
    private static void writeToFile(File path, String content, Supplier<BufferedWriter> bufferFactory) {
    	try {
        	path.getParentFile().mkdirs();
            String oldContent = readFromFile(path);
            if (!content.equals(oldContent)) {
                System.out.println("Updating " + path);
                BufferedWriter out = bufferFactory.get();
                out.write(content);
                out.close();
            } else {
                System.out.println(path + " unchanged: No update required!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Not Found " + path);
        } catch (IOException e) {
            System.out.println("IOException for " + path);
        }
    }
    
    protected static void writeToFile(File path, String content) {
    	writeToFile(path, content, () -> {
			try {
				return new BufferedWriter(new FileWriter(path));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});
    }
    
    protected static void writeToFileInUTF8(File path, String content) {
		CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPORT);
		encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		writeToFile(path, content, encoder);
    }
    
    protected static void writeToFile(File path, String content, CharsetEncoder encoder) {
    	writeToFile(path, content, () -> {
			try {
				return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), encoder));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});
    }

    public abstract void writeDocument();
}
