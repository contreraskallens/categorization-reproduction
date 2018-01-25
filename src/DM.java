import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class DM implements Steppable {
    private Bag categorizers;
    private SimState state;
    private double minSeparation = Categorization.dmin;

    public DM(SimState state, Bag categorizers){
        this.categorizers = categorizers;
        this.state = state;
    }

    public void step(SimState state){
        int index1 = 0;
        int index2;
        do {
            index2 = state.random.nextInt(categorizers.size());
        } while(index1 == index2);
        index1 = state.random.nextInt(categorizers.size());

        Categorizer categorizer1 = (Categorizer) categorizers.get(index1);
        Categorizer categorizer2 = (Categorizer) categorizers.get(index2);

        playGame(categorizer1, categorizer2);
        Globals.numberOfGames++;
    }

    private void playGame(Categorizer categorizer1, Categorizer categorizer2) {
        double object1;
        double object2;

        boolean success; // track if game is successful
        object1 = state.random.nextDouble(false, false);

        do {
            object2 = state.random.nextDouble(false, false);
        } while (abs(object1 - object2) < minSeparation);

        //object1 = round(object1 * 1000);
        //object2 = round(object2 * 1000);

        //object1 = object1 / 1000;
        //object2 = object2 / 1000;
        //if(object1 == 1.0){
        //    object1 = 0.999;
        //}
        //if(object2 == 1.0){
        //    object2 = 0.999;
        //}

        if (!categorizer1.discriminate(object1, object2)) {
            categorizer1.divideCategory(object1, object2);
        }

        double[] objects = new double[]{object1, object2};
        int topicIndex = state.random.nextInt(2);
        double topic = objects[topicIndex];
        String categorizerUtterance = categorizer1.categorize(topic).preferredLabel;

        double pointedObject = -1;

        Bag categorizer2Cats = categorizer2.matchWord(categorizerUtterance);

        Category category2OfObject1 = categorizer2.categorize(object1);
        Category category2OfObject2 = categorizer2.categorize(object2);

        boolean labelCategoryMatch;
        if (categorizer2Cats.isEmpty()) {
            labelCategoryMatch = false;
        } else if(categorizer2Cats.contains(category2OfObject1) || categorizer2Cats.contains(category2OfObject2)) {
            labelCategoryMatch = true;
        } else {
            labelCategoryMatch = false;
        }

        // pointing //

        if (category2OfObject1.equals(category2OfObject2) && labelCategoryMatch) { // if they matched but they are the same
            pointedObject = objects[state.random.nextInt(2)]; // choose one at random
        }

        boolean uniqueCategory;

        if ((categorizer2Cats.contains(category2OfObject1) ^ (categorizer2Cats.contains(category2OfObject2))) && labelCategoryMatch) { // if there is only one category
            uniqueCategory = true;
            if (categorizer2Cats.contains(category2OfObject1)) {
                pointedObject = object1;
            } else if (categorizer2Cats.contains(category2OfObject2)) {
                pointedObject = object2;
            }
        } else {
            uniqueCategory = false;
        }

        if (!uniqueCategory && labelCategoryMatch) { // if you have more than one category that matches
            pointedObject = objects[state.random.nextInt(2)]; // choose one at random
        }


        // comparing and resolving //

        if (!labelCategoryMatch) {
            success = false;
        } else if (pointedObject == topic) {
            success = true;

        } else if (pointedObject != topic) {
            success = false;
        }
            else if (pointedObject == -1 && labelCategoryMatch) {
            success = false;
        } else {
            success = false;
            System.out.println("hey something went wrong in what");
            System.out.println("labelMatch: " + labelCategoryMatch);
            System.out.println("uniqueCategory: " + uniqueCategory);
            System.out.println("object1: " + object1);
            System.out.println("object2: " + object2);
            System.out.println("topic: " + topic);
            System.out.println("pointed object: " + pointedObject);
            System.out.println("---------- \n \n \n");
        }

        // updating after game //

        if(!categorizer2.discriminate(object1, object2)){
            categorizer2.divideCategory(object1, object2); // if heared doesn't discriminate, do it so you discriminate.
        }

        Category categoryOf1 = categorizer1.categorize(topic);
        Category categoryOf2 = categorizer2.categorize(topic);



        if (success){

            categoryOf1.setPreferredLabel(categorizerUtterance);
            categoryOf2.setPreferredLabel(categorizerUtterance);
            Bag newLabel = new Bag();
            newLabel.add(categorizerUtterance);
            categoryOf1.labels = newLabel;
            categoryOf2.labels = newLabel;

            Globals.lastThousandGames.add(1);

        } else if (!success){
            Globals.lastThousandGames.add(0);
            if(!categoryOf2.labels.contains(categorizerUtterance)){ // avoid duplicates in inventories
                categoryOf2.labels.add(categorizerUtterance);}

        } else {
            System.out.println("hey somethiung went wrong");}
        categorizer1.createLinguisticCategories();
        categorizer2.createLinguisticCategories();
        categorizer1.updatePartitions();
        categorizer2.updatePartitions();
    }
}
