import sim.util.*;

public class Category {
    public double[] perceptualRange;
    public Bag labels;
    public String preferredLabel;

    public Category(double lowEnd, double highEnd, Bag labels){
        perceptualRange = new double[] {lowEnd, highEnd};
        this.labels = new Bag();
        this.labels.addAll(labels);
        if(labels.size() == 1){
            this.preferredLabel = (String) labels.get(0);
        }
    }

    public double[] getPerceptualRange(){
        return perceptualRange;
    }

    public void setLabels(Bag labels) {
        this.labels = labels;
    }

    public String getPreferredLabel(){
        return preferredLabel;
    }

    public void setPreferredLabel(String preferredLabel) {
        this.preferredLabel = preferredLabel;
    }

    public Category merge(Category otherCategory){
        Double[] allRanges = new Double[] {this.perceptualRange[0], this.perceptualRange[1], otherCategory.perceptualRange[0], otherCategory.perceptualRange[1]};
        java.util.Arrays.sort(allRanges);
        Category newCategory = new Category(allRanges[0], allRanges[3], this.labels);
        return newCategory;
    }

    public Category lookForAdjacent(Bag otherCategories){
        for(int i = 0; i < otherCategories.size(); i++){
            Category otherCat = (Category) otherCategories.get(i);
            if((this.perceptualRange[0] == otherCat.perceptualRange[1]) || (this.perceptualRange[1] == otherCat.perceptualRange[0])){
                return otherCat;
            }
        }
        return null;
    }

    public String toString(){
        StringBuilder theString = new StringBuilder();
        theString.append("Perceptual Range: [ ");
        for (int i = 0; i < 2; i++){
            theString.append(perceptualRange[i]);
            theString.append(" ");
        }
        theString.append("] \n");

        theString.append("Labels: ");
        for(int i = 0; i < labels.size(); i++) {
            theString.append(labels.get(i));
            if (i < (labels.size() - 1)) {
                theString.append(",");
            } else {
                theString.append(". \n");
            }
        }
        theString.append("Preferred Label: ");
        theString.append(preferredLabel);
        theString.append("\n ");
        return theString.toString();
    }
}
