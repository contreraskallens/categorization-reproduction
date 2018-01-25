import sim.engine.*;
import sim.util.*;

import java.io.IOException;

public class Categorization extends SimState {
    public int numberOfCategorizers = 100;
    public Bag categorizers = new Bag();
    static double dmin = 0.001; // minimum separation


    public Categorization(long seed){
            super(seed);
    }

    public static void main(String[] args){
        doLoop(Categorization.class, args);
        System.exit(0);
    }

    public void start() {
        super.start();

        //allocate data file//

        try{ Outputter io = new Outputter();
            io.checkEmpty();
        } catch (IOException excep){excep.printStackTrace();}


        // allocate the categorizers in a bag of categorizers //


        for(int i = 0; i < numberOfCategorizers; i++){
            Categorizer categorizer = new Categorizer(this);
            categorizers.add(categorizer);
        }
        DM dungeonMaster = new DM(this, categorizers);
        Globals globalPars = new Globals(this, categorizers);
        schedule.scheduleRepeating(dungeonMaster);
        schedule.scheduleRepeating((Steppable) globalPars, 1, 10000); // if running without visualization, better to put interval = 1000 ~ 10000

    }

    public void finish(){
        super.finish();
        Outputter.runNumber += 1;
    }

    public void setDmin(Double minSep){
        dmin = minSep;
    }

    public double getAvgLingCategories() {
        double avgCats = Globals.avgLingCategories;
        avgCats = avgCats * 1000;
        avgCats = Math.round(avgCats);
        avgCats = avgCats / 1000;
        return avgCats;
    }

    public double getAvgPercCategories() {
        double avgCats = Globals.avgPercCategories;
        avgCats = avgCats * 1000;
        avgCats = Math.round(avgCats);
        avgCats = avgCats / 1000;
        return avgCats;
    }

    public double getAvgSynonymy() {
        double syn = Globals.avgSynonymy;
        syn = syn * 1000;
        syn = Math.round(syn);
        syn = syn / 1000;
        return syn;
    }

    public int getGamesPerPlayer(){
        return Globals.gamesPerPlayer;
    }
    public double getSuccessRate(){
        double theRate = Globals.successRate;
        theRate = theRate * 1000;
        theRate = Math.round(theRate);
        theRate = theRate / 1000;
        return theRate;
    }

    public double getPercOverlap(){
        double theOverlap = Globals.globalPercOverlap;
        theOverlap = theOverlap * 1000;
        theOverlap = Math.round(theOverlap);
        theOverlap = theOverlap / 1000;
        return theOverlap;
    }

    public double getLingOverlap(){
        double theOverlap = Globals.globalLingOverlap;
        theOverlap = theOverlap * 1000;
        theOverlap = Math.round(theOverlap);
        theOverlap = theOverlap / 1000;
        return theOverlap;
    }
}

