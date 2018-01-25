import sim.engine.*;
import sim.util.*;
import java.util.*;

public class Categorizer {
    public Bag perceptualCategories = new Bag(); // bag to store categories
    private Bag linguisticCategories = new Bag();
    private SimState state;
    public int numberOfPercCategories;
    public int numberOfLingCategories;
    public double avgSynonymy;
    public DoubleBag perceptualPartition;
    public DoubleBag linguisticPartition;

    public Categorizer(SimState state) {
        this.state = state;
        Bag newLabel = new Bag();
        newLabel.add(randomString());
        Category perceptualCategory = new Category((double) 0, (double) 1, newLabel);
        perceptualCategories.add(perceptualCategory);
        createLinguisticCategories();
        updatePartitions();
    }

    private String randomString() {
        char[] acceptableSymbols = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z'};
        StringBuilder theString = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            int index = state.random.nextInt(acceptableSymbols.length);
            theString.append(acceptableSymbols[index]);
        }
        return theString.toString();
    }

    public String toString() {
        StringBuilder theString = new StringBuilder();
        for (int i = 0; i < perceptualCategories.size(); i++) {
            theString.append(perceptualCategories.get(i).toString());
        }
        return theString.toString();
    }

    public boolean discriminate(Double object1, Double object2) {
        Category category1 = categorize(object1);
        Category category2 = categorize(object2);
        if (category1.equals(category2)) {
            return false;

        } else {
            return true;
        }

    }

    public Category categorize(Double object) {
        for (int i = 0; i < perceptualCategories.size(); i++) {
            Category category = (Category) perceptualCategories.get(i);
            if (object < category.perceptualRange[1] && object >= category.perceptualRange[0]) {
                return category;
            }
        }
        return null;
    }


    public void divideCategory(double object1, double object2) {
        Category categoryToBeDivided = categorize(object1);
        double[] previousRange = categoryToBeDivided.perceptualRange;
        Bag previousLabels = categoryToBeDivided.labels;
        double newLimit;
        if (object1 > object2) {
            newLimit = object2 + ((object1 - object2) / 2);
        } else {
            newLimit = object1 + ((object2 - object1) / 2);
        }

        String newLabel1 = randomString();
        String newLabel2 = randomString();
        Bag newLabels1 = new Bag();
        Bag newLabels2 = new Bag();
        newLabels1.addAll(previousLabels);
        newLabels2.addAll(previousLabels);
        newLabels1.add(newLabel1);
        newLabels2.add(newLabel2);
        Category newCat1 = new Category(previousRange[0], newLimit, newLabels1);
        newCat1.setPreferredLabel(newLabel1);
        Category newCat2 = new Category(newLimit, previousRange[1], newLabels2);
        newCat2.setPreferredLabel(newLabel2);


        perceptualCategories.remove(categoryToBeDivided);
        perceptualCategories.add(newCat1);
        perceptualCategories.add(newCat2);
    }

    public void createLinguisticCategoriesold() {
        Bag lingCategories = new Bag();
        lingCategories.addAll(this.perceptualCategories);
        int accessCategory = 0;

        do{
            if(accessCategory == lingCategories.size() - 1){ // if it's the last one
                break;
            }
            lingCategories.sort(new Comparator<Category>() { // sorts descending order
                public int compare(Category cat1, Category cat2) {
                    Double highLimit1 = cat1.perceptualRange[1];
                    Double lowLimit1 = cat1.perceptualRange[0];
                    Double sum1 = highLimit1 + lowLimit1;

                    Double lowLimit2 = cat2.perceptualRange[0];
                    Double highLimit2 = cat2.perceptualRange[1];
                    Double sum2 = highLimit2 + lowLimit2;

                    return sum1.compareTo(sum2);
                }
            });

            lingCategories.reverse();

            Category comparingCategory = (Category) lingCategories.get(accessCategory);

            System.out.println("compared: ");
            System.out.println(comparingCategory);

            if (comparingCategory.labels.size() == 1) { // if it only has one word
                System.out.println("the other one: ");
                Category comparedCategory = (Category) lingCategories.get(accessCategory + 1);
                System.out.println(comparedCategory);
                System.out.println("--------- \n \n");
                if (comparedCategory.labels.size() == 1) {
                    String word1 = (String) comparingCategory.labels.get(0);
                    String word2 = (String) comparedCategory.labels.get(0);
                    if (word1.equals(word2)) {
                        Category newCategory = comparingCategory.merge(comparedCategory);
                        lingCategories.removeNondestructively(comparedCategory);
                        lingCategories.removeNondestructively(comparingCategory);
                        lingCategories.add(newCategory);
                        continue;
                    }

                }
            }
            accessCategory++;
        } while (accessCategory <= lingCategories.size() - 1);

        this.linguisticCategories = lingCategories;
    }


    public void createLinguisticCategories() {
        Bag lingCategories = new Bag();
        int accessCategory = 0;

        do{
            this.perceptualCategories.sort(new Comparator<Category>() { // sorts descending order
                public int compare(Category cat1, Category cat2) {
                    Double highLimit1 = cat1.perceptualRange[1];
                    Double lowLimit2 = cat2.perceptualRange[0];
                    return lowLimit2.compareTo(highLimit1);
                }
            });
            this.perceptualCategories.reverse();

            Category comparingCategory = (Category) perceptualCategories.get(accessCategory);

            if(accessCategory == perceptualCategories.size() - 1){ // if it's the last one
                lingCategories.add(comparingCategory);
                break;
            }

            if (comparingCategory.labels.size() == 1) { // if it only has one word
                Category comparedCategory = (Category) perceptualCategories.get(accessCategory + 1);
                if (comparedCategory.labels.size() == 1) {
                    String word1 = (String) comparingCategory.labels.get(0);
                    String word2 = (String) comparedCategory.labels.get(0);
                    if (word1.equals(word2)) {
                        boolean nextOneIsPart = true;
                        Category newCategory;
                        newCategory = comparingCategory.merge(comparedCategory);
                        do{
                            accessCategory++;
                            if(accessCategory >= perceptualCategories.size() - 1){ // if it's the last one
                                lingCategories.add(newCategory);
                                break;
                            }
                            Category nextCategory = (Category) perceptualCategories.get(accessCategory + 1);
                            String word3 = (String) nextCategory.labels.get(0);
                            if(nextCategory.labels.size() == 1 && word3.equals(word1)){
                                newCategory = newCategory.merge(nextCategory);
                                continue;
                            } else {
                                lingCategories.add(newCategory);
                                nextOneIsPart = false;
                            }
                        } while (nextOneIsPart);
                    } else { // if adjacent has 1 word, but it's not the same word
                        lingCategories.add(comparingCategory);
                    }
                } else { // if adjacent doesn't have 1 word
                    lingCategories.add(comparingCategory);
                }
            } else {// if first one has more than 1 word
                lingCategories.add(comparingCategory);
            }
            accessCategory++;
        } while (accessCategory < perceptualCategories.size());

        this.linguisticCategories = lingCategories;
    }


    public Bag matchWord(String word) {

        Bag matchedCategories = new Bag();
        for (int i = 0; i < this.perceptualCategories.size(); i++) {
            Category scannedCategory = (Category) this.perceptualCategories.get(i);
            Bag scannedLabels = scannedCategory.labels;
            boolean containsWord = false;
            for (int x = 0; x < scannedLabels.size(); x++) {
                String scannedWord = (String) scannedLabels.get(x);
                if (scannedWord.equals(word)) {
                    containsWord = true;
                }
            }
            if(containsWord){
                matchedCategories.add(scannedCategory);
            }
        }
        return matchedCategories;
    }

    public double overlapsWithOthers(Bag otherCategorizers, String mode){
        double overlaps = 0;
        for(int i = 0; i < otherCategorizers.size(); i++){
            Categorizer otherCategorizer = (Categorizer) otherCategorizers.get(i);
            if(mode.equals("perceptual")){
                overlaps += measureOverlap(otherCategorizer.perceptualPartition, this.perceptualPartition);
            }
            if(mode.equals("linguistic")){
                overlaps += measureOverlap(otherCategorizer.linguisticPartition, this.linguisticPartition);
            }
        }
        return overlaps;
    }


    public void updatePartitions() {
        DoubleBag percBag = new DoubleBag();
        for (int i = 0; i < this.perceptualCategories.size(); i++) {
            Category percCat = (Category) perceptualCategories.get(i);
            Double lowerLimit = percCat.perceptualRange[0];
            percBag.add(lowerLimit);
        }
        percBag.add(1.0); //add upper limit
        this.perceptualPartition = percBag;

        DoubleBag lingBag = new DoubleBag();
        for (int i = 0; i < this.linguisticCategories.size(); i++) {
            Category lingCat = (Category) linguisticCategories.get(i);
            Double lowerLimit = lingCat.perceptualRange[0];
            lingBag.add(lowerLimit);
        }
        lingBag.add(1.0); // add upper limit
        lingBag.sort();
        this.linguisticPartition = lingBag;
    }

    public double measureOverlap(DoubleBag otherPartition, DoubleBag myPartition){
        DoubleBag overlapPartition = new DoubleBag();
        overlapPartition.addAll(otherPartition);
        overlapPartition.addAll(myPartition);

        overlapPartition.sort();
        overlapPartition.remove(0); // remove one 0.0
        overlapPartition.remove(overlapPartition.size() - 1); // remove one 1.0

        Double myDistance = calculateDistances(myPartition);
        Double otherDistance = calculateDistances(otherPartition);
        Double overlapDistance = calculateDistances(overlapPartition);

        Double overlap = (2 * overlapDistance) / (myDistance + otherDistance);

        return overlap;
    }

    private Double calculateDistances(DoubleBag limits) {

        limits.sort();
        Double sumOfDistances = 0.0;
        for (int i = 0; i < limits.size(); i++) {
            if (i == limits.size() - 1) {
                break;
            }
            Double limit1 = limits.get(i);
            Double limit2 = limits.get(i + 1);
            Double distance = limit1 - limit2;
            distance = distance * distance;
            sumOfDistances = sumOfDistances + distance;
        }

        return sumOfDistances;
    }


    public void updateStats(){
        // createLinguisticCategories();
        //updatePartitions();
        this.numberOfPercCategories = perceptualCategories.size();
        this.numberOfLingCategories = linguisticCategories.size();
        this.avgSynonymy = 0;

        for(int i = 0; i < this.perceptualCategories.size(); i++){
            Category category = (Category) this.perceptualCategories.get(i);
            this.avgSynonymy += category.labels.size();
        }
        this.avgSynonymy = this.avgSynonymy / perceptualCategories.size();
    }
}