import java.io.*;


public class Outputter {
    static int runNumber = 0; // tracks run number for batches
    private String fileName;

    public Outputter() throws IOException {

        fileName = "resources\\run" + runNumber + ".csv";
        BufferedWriter pw = null;

        try {
            pw = new BufferedWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeGlobals(pw);
        closeFile(pw);

    }

    public void checkEmpty() throws IOException {
        Boolean empty = false;
        BufferedReader pr = new BufferedReader(new FileReader(this.fileName));
        if (pr.readLine() == null) {
            empty = true;
        } else {
            empty = false;
        }
        if (empty = true) {
            BufferedWriter pw = new BufferedWriter(new FileWriter(this.fileName));
            pw.write("gamesPerPlayer\t" + "avgPerceptualCats\t" + "avgLinguisticCats\t" + "avgSynonymy\t" + "successRate\t" + "percOverlap\t" + "lingOverlap");
            pw.newLine();
            pw.flush();
        } else {
            System.out.println("clean file first");
        }
    }

    private void writeGlobals(BufferedWriter pw) throws IOException{
        pw.write(Globals.gamesPerPlayer + "\t" + Globals.avgPercCategories + "\t" + Globals.avgLingCategories + "\t" + Globals.avgSynonymy + "\t" + Globals.successRate + "\t" + Globals.globalPercOverlap + "\t" + Globals.globalLingOverlap + "\n");
        pw.flush();
    }

    private void closeFile(BufferedWriter pw) throws IOException{
        pw.flush();
        pw.close();
    }
}
