import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.*;

import java.io.IOException;

public class Globals implements Steppable {
    static int numberOfGames;
    static double avgPercCategories;
    static double avgLingCategories;
    static double avgSynonymy;
    static Bag allCategorizers;
    static int gamesPerPlayer;
    static IntBag lastThousandGames;
    static double successRate;
    static double globalPercOverlap;
    static double globalLingOverlap;

    public Globals(SimState state, Bag categorizers){
        numberOfGames = 0;
        avgPercCategories = 0;
        avgLingCategories = 0;
        avgSynonymy = 0;
        this.allCategorizers = categorizers;
        gamesPerPlayer = 0;
        globalPercOverlap = 0;
        globalLingOverlap = 0;
        avgLingCategories = 0;
        lastThousandGames = new IntBag();
    }
    public void step(SimState state){
        updateGlobals();
        try{ Outputter io = new Outputter();
        io = null;} catch(IOException io){io.printStackTrace();}
    }

    public void updateGlobals(){
        double avgPercCats = 0;
        double avgLingCats = 0;
        double avgSyn = 0;
        double percOverlap = 0;
        double lingOverlap = 0;

        int n = allCategorizers.size();

        for(int i = 0; i < n;i++){
            Categorizer categorizer = (Categorizer) allCategorizers.get(i);
            categorizer.updateStats();

            avgPercCats += categorizer.numberOfPercCategories;
            avgLingCats += categorizer.numberOfLingCategories;
            avgSyn += categorizer.avgSynonymy;

        }


        Bag modifiableBag = new Bag();
        modifiableBag.addAll(allCategorizers);

        while(modifiableBag.size() > 1){
            Categorizer categorizer = (Categorizer) modifiableBag.pop();
            percOverlap += categorizer.overlapsWithOthers(modifiableBag, "perceptual");
            lingOverlap += categorizer.overlapsWithOthers(modifiableBag, "linguistic");
        }

        avgPercCats = avgPercCats / n;
        avgLingCats = avgLingCats / n;
        avgSyn = avgSyn / n;

        percOverlap = percOverlap / (n * (n - 1));
        lingOverlap = lingOverlap / (n * (n - 1));

        percOverlap = percOverlap * 2;
        lingOverlap = lingOverlap * 2;

        Globals.avgPercCategories = avgPercCats;
        Globals.avgLingCategories = avgLingCats;
        Globals.avgSynonymy = avgSyn;
        Globals.gamesPerPlayer = numberOfGames / allCategorizers.size();
        Globals.globalLingOverlap = lingOverlap;
        Globals.globalPercOverlap = percOverlap;

        successRate = 0;
        do{
            lastThousandGames.removeNondestructively(0);
        } while(lastThousandGames.size() > 1000);
        for(int i = 0; i < lastThousandGames.size(); i++){
            successRate = successRate + lastThousandGames.get(i);
        }
        successRate = successRate / 1000;
    }
}
