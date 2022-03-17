package hasan.mohamed.shehata.myapplication.types;

public class SearchCallbacks {
    public static interface Searchable {
        public void find(String query);
    }

    public Searchable getSearchable() {
        return searchable;
    }

    public void setSearchable(Searchable searchable) {
        this.searchable = searchable;
    }

    private Searchable searchable;
}
