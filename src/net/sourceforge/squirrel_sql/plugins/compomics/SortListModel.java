package net.sourceforge.squirrel_sql.plugins.compomics;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Hulstaert on 6/05/15.
 */
public class SortListModel extends AbstractListModel {

    public enum Sorting {
        ASC, DESC;
    }

    private List<String> fields = new ArrayList<String>();
    private List<Sorting> sortings = new ArrayList<Sorting>();

    public List<String> getFields() {
        return fields;
    }

    public List<Sorting> getSorting() {
        return sortings;
    }

    /**
     * Add an entry to the model.
     *
     * @param field
     * @param sorting
     */
    public void add(String field, Sorting sorting) {
        fields.add(field);
        sortings.add(sorting);
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Remove the entry with the given index.
     *
     * @param index
     */
    public void remove(int index) {
        fields.remove(index);
        sortings.remove(index);
        fireContentsChanged(this, 0, getSize());
    }

    @Override
    public int getSize() {
        return fields.size();
    }

    @Override
    public Object getElementAt(int index) {
        String field = fields.get(index);

        return field + " " + sortings.get(index).toString();
    }
}
