package net.sourceforge.squirrel_sql.plugins.compomics;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Hulstaert on 6/05/15.
 */
public class ParamsListModel extends AbstractListModel {

    private List<String> fields = new ArrayList<String>();
    private List<List<String>> params = new ArrayList<List<String>>();

    public List<String> getFields() {
        return fields;
    }

    public List<List<String>> getParams() {
        return params;
    }

    /**
     * Add an entry to the model.
     *
     * @param field
     * @param parameters
     */
    public void add(String field, List<String> parameters) {
        fields.add(field);
        params.add(parameters);
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Remove the entry with the given index.
     *
     * @param index
     */
    public void remove(int index) {
        fields.remove(index);
        params.remove(index);
        fireContentsChanged(this, 0, getSize());
    }

    @Override
    public int getSize() {
        return fields.size();
    }

    @Override
    public Object getElementAt(int index) {
        String field = fields.get(index);

        return field + " " + params.get(index).toString();
    }
}
