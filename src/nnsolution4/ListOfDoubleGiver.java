package nnsolution4;

import java.util.List;

class ListOfDoubleGiver {
    private List<List<Double>> list;
    private int index;

    ListOfDoubleGiver(List<List<Double>> list) {
        this.list = list;
        index = 0;
    }

    List<Double> give() {
        return list.get(index++);
    }
}
