package webstand.cases;

import files.FilesIO;
import main.Main;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CasesFunctions {
    private static final String casesSource = "cases.txt";

    public static List<String> getAllCases() {
        try {
            Scanner casesFile = new Scanner(new File(casesSource));


            ArrayList<String> cases = new ArrayList<>();
            while (casesFile.hasNext()) {
                String line = casesFile.nextLine();
                cases.add(getCaseFromStandardLine(line));
            }
            return cases;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDoctorAndCct(String caseName) {
        String tmp = findPathWithCase(caseName);
        String slash;
        if (Main.windows) {
            slash = "\\";
        } else slash = "/";
        tmp = tmp.substring(0, tmp.indexOf(slash + "xslt"));
        tmp = tmp.substring(tmp.lastIndexOf(slash) + 1);
        return tmp;
    }

    public static String getDoctor(String caseName) {
        String tmp = getDoctorAndCct(caseName).trim();
        int whitePos = tmp.indexOf(" ");
        return tmp.substring(whitePos).trim();
    }

    public static void updateCaseNames() {
        File caseNames = new File(casesSource);
        try {
            PrintWriter printWriter = new PrintWriter(caseNames);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes fileAttributes) {
                String filePath = visitedFile.toFile().getPath();
                if (filePath.endsWith(".xslt")) {
                    if (visitedFile.getParent().toFile().getPath().contains("84 Emergency call form")) {
                        return FileVisitResult.CONTINUE;
                    }
                    String caseNow = findCaseInXslt(filePath);
                    if (caseNow.isEmpty()) {
                        System.out.println("Can't find case name in " + filePath);
                        return FileVisitResult.CONTINUE;
                    }

                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(caseNames, true));
                        String toAppend = "";
                        if (Files.readAllLines(caseNames.toPath()).size() != 0) {
                            toAppend = "\n";
                        }
                        toAppend += "test_" + caseNow.replaceAll(" ", "") + " ; " + filePath;
                        writer.append(toAppend);
                        writer.close();
                    } catch (Exception ignored) {
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };

        FileSystem fileSystem = FileSystems.getDefault();
        Path rootPath = fileSystem.getPath(FilesIO.getPathAll());
        try {
            Files.walkFileTree(rootPath, simpleFileVisitor);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static String getCaseFromStandardLine(String line) {
        return line.substring(0, line.indexOf(" ; ")).replaceAll(" ", "");
    }

    private static String getPathFromStandardLine(String line) {
        return line.substring(line.indexOf(" ; ") + 3);
    }

    public static String findCaseWithPath(String path) {
        File file = new File(casesSource);
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(path)) {
                    return getCaseFromStandardLine(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String findPathWithCase(String caseName) {
        File file = new File(casesSource);
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(caseName) & !(line.contains(caseName + "s"))) {
                    return getPathFromStandardLine(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getXsdPath(String filePath) {
        String res = filePath.substring(0, filePath.lastIndexOf(FilesIO.delim));
        res = res.substring(0, res.lastIndexOf(FilesIO.delim));
        res += FilesIO.delim + "xml" + FilesIO.delim + "tds";
        String xsdName = FilesIO.listFilesForFolder(new File(res)).get(0);
        res += FilesIO.delim + xsdName;
        return res;
    }

    private static String findCaseInXslt(String filePath) {
        File file = new File(filePath);
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(".xslt_")) {
                    return line.substring(line.indexOf(".xslt_") + 6,
                            line.indexOf("{", line.replaceAll(" ", "").indexOf(".xslt_")))
                            .replaceAll("[^A-Za-z0-9]", "");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
