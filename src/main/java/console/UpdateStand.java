package console;

import files.FilesIO;
import repository.Git;
import webstand.Session;
import webstand.Updater;
import webstand.cases.CasesFunctions;

import java.util.List;
import java.util.Scanner;

import static webstand.Stand.showDiffCommand;
import static webstand.cases.CasesFunctions.findPathWithCase;

class UpdateStand {
    static void updateCasesOnStandWithGit() {
        List<String> changedCases = Git.getChangedCasesGit();
        standardUpdaterCases(changedCases);
    }

    private static void standardUpdaterCases(List<String> changedCases) {
        if (changedCases.size() != 0) {
            System.out.println("Cases to update: " + changedCases);
            for (String caseName : changedCases) {
                Session session = new Session(caseName);
                String localXslt = FilesIO.readXslt(findPathWithCase(caseName));
                if (!localXslt.contentEquals(session.getXsltString())) {
                    System.out.println(showDiffCommand(caseName, session.getXsltString(), localXslt));
                    System.out.print("Update " + caseName + " (" + CasesFunctions.getDoctorAndCct(caseName) + ")? [y/n]: ");
                    if ((new Scanner(System.in)).next().equals("y")) {
                        Git.commit(caseName);
                        Updater.updateXslt(session);
                        session.saveCase();
                        System.out.println("Updated!");
                    } else {
                        System.out.println("Canceled.");
                    }
                }
            }
        } else {
            System.out.println("Nothing to update!");
        }
    }
}
