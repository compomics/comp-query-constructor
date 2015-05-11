package net.sourceforge.squirrel_sql.plugins.compomics;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryConstructorDialog extends JDialog {

    private static final String PARAM_SEPARATOR = ",";
    private static final String YEAR_FIELD = "PUBLICATIONYEAR";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField selectTextField;
    private JTextField fromTextField;
    private JButton removeEqualsButton;
    private JButton addEqualsButton;
    private JTextField equalsParamsTextField;
    private JList equalsParamsList;
    private JLabel equalsFieldLabel;
    private JLabel equalsParametersLabel;
    private JLabel equalsLabel2;
    private JLabel likeLabel2;
    private JList likeParamsList;
    private JButton addLikeButton;
    private JButton removeLikeButton;
    private JTextField likeParamsTextField;
    private JTextField fromYearTextField;
    private JTextField toYearTextField;
    private JLabel fromYearLabel;
    private JLabel toYearLabel;
    private JComboBox equalsFieldComboBox;
    private JComboBox likeFieldComboBox;
    private JList orderParamsList;
    private JComboBox orderFieldComboBox;
    private JComboBox sortingComboBox;
    private JButton removeOrderButton;
    private JButton addOrderButton;

    private ParamsListModel equalsParamsListModel = new ParamsListModel();
    private ParamsListModel likeParamsListModel = new ParamsListModel();
    private SortListModel sortListModel = new SortListModel();
    private DefaultComboBoxModel equalsFieldComboBoxModel;
    private DefaultComboBoxModel likeFieldComboBoxModel;
    private DefaultComboBoxModel orderFieldComboBoxModel;
    private DefaultComboBoxModel orderComboBoxModel;

    /**
     * The command instance;
     */
    private OpenQueryDialogCommand command;

    public QueryConstructorDialog(Frame parenFrame, OpenQueryDialogCommand command) {
        super(parenFrame, "Compomics query constructor", true);

        this.command = command;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        init();
    }

    private void init() {
        // set list models
        equalsParamsList.setModel(equalsParamsListModel);
        likeParamsList.setModel(likeParamsListModel);
        orderParamsList.setModel(sortListModel);

        equalsFieldComboBoxModel = new DefaultComboBoxModel(command.getColumnNames());
        equalsFieldComboBox.setModel(equalsFieldComboBoxModel);
        likeFieldComboBoxModel = new DefaultComboBoxModel(command.getColumnNames());
        likeFieldComboBox.setModel(likeFieldComboBoxModel);
        orderFieldComboBoxModel = new DefaultComboBoxModel(command.getColumnNames());
        orderFieldComboBox.setModel(orderFieldComboBoxModel);
        orderComboBoxModel = new DefaultComboBoxModel(SortListModel.Sorting.values());
        sortingComboBox.setModel(orderComboBoxModel);

        // set default table name
        fromTextField.setText(OpenQueryDialogAction.TABLE_NAME);

        addEqualsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String field = equalsFieldComboBoxModel.getSelectedItem().toString();
                String parameters = equalsParamsTextField.getText();
                if (!field.isEmpty() && !parameters.isEmpty()) {
                    if (!equalsParamsListModel.getFields().contains(field)) {
                        // validate input
                        if (isValidParamString(parameters)) {
                            Map<String, List<String>> equalsParams = new HashMap<String, List<String>>();
                            List<String> params;
                            Iterable<String> paramsIterable = Splitter.on(PARAM_SEPARATOR)
                                    .trimResults()
                                    .omitEmptyStrings()
                                    .split(parameters);
                            params = Lists.newArrayList(paramsIterable);
                            equalsParams.put(field, new ArrayList<String>());
                            equalsParamsListModel.add(field, params);

                            //reset text fields
                            equalsFieldComboBox.setSelectedIndex(0);
                            equalsParamsTextField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(QueryConstructorDialog.this, "Equals parameters field is not filled in correctly. (Use '" + PARAM_SEPARATOR + "' as delimiter)", "Validation error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(QueryConstructorDialog.this, "The field " + field + " is already present in the list.", "Already present", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(QueryConstructorDialog.this, "Please enter a field and one or more parameter values (Use '" + PARAM_SEPARATOR + "' as delimiter)", "Empty selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        removeEqualsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int index = equalsParamsList.getSelectedIndex();
                if (index != -1) {
                    equalsParamsListModel.remove(index);
                    equalsParamsList.getSelectionModel().clearSelection();
                } else {
                    JOptionPane.showMessageDialog(QueryConstructorDialog.this, "Please select an equals part to remove.", "Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        addLikeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String field = likeFieldComboBoxModel.getSelectedItem().toString();
                String parameters = likeParamsTextField.getText();
                if (!field.isEmpty() && !parameters.isEmpty()) {
                    if (!likeParamsListModel.getFields().contains(field)) {
                        // validate input
                        if (isValidParamString(parameters)) {
                            Map<String, List<String>> likeParams = new HashMap<String, List<String>>();
                            List<String> params;
                            Iterable<String> paramsIterable = Splitter.on(PARAM_SEPARATOR)
                                    .trimResults()
                                    .omitEmptyStrings()
                                    .split(parameters);
                            params = Lists.newArrayList(paramsIterable);
                            likeParams.put(field, new ArrayList<String>());
                            likeParamsListModel.add(field, params);

                            //reset text fields
                            likeFieldComboBox.setSelectedIndex(0);
                            likeParamsTextField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(QueryConstructorDialog.this, "Like parameters field is not filled in correctly. (Use '" + PARAM_SEPARATOR + "' as delimiter)", "Validation error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(QueryConstructorDialog.this, "The field " + field + " is already present in the list.", "Already present", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(QueryConstructorDialog.this, "Please enter a field and one or more parameter values (Use '" + PARAM_SEPARATOR + "' as delimiter)", "Empty selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        removeLikeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int index = likeParamsList.getSelectedIndex();
                if (index != -1) {
                    likeParamsListModel.remove(index);
                    likeParamsList.getSelectionModel().clearSelection();
                } else {
                    JOptionPane.showMessageDialog(QueryConstructorDialog.this, "Please select a like part to remove.", "Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        addOrderButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String field = orderFieldComboBoxModel.getSelectedItem().toString();
                SortListModel.Sorting sorting = (SortListModel.Sorting) orderComboBoxModel.getSelectedItem();
                if (!sortListModel.getFields().contains(field)) {
                    sortListModel.add(field, sorting);

                    //reset selection
                    orderFieldComboBox.setSelectedIndex(0);
                    sortingComboBox.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(QueryConstructorDialog.this, "The field " + field + " is already present in the list.", "Already present", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        removeOrderButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int index = orderParamsList.getSelectedIndex();
                if (index != -1) {
                    sortListModel.remove(index);
                    orderParamsList.getSelectionModel().clearSelection();
                } else {
                    JOptionPane.showMessageDialog(QueryConstructorDialog.this, "Please select an order part to remove.", "Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

    }

    private void onOK() {
        // validate input
        List<String> validationMessages = validateInput();

        if (validationMessages.isEmpty()) {
            QueryInput queryInput = onValid();

            // pass QueryInput to OpenQueryDialogCommand
            command.setQueryInput(queryInput);

            dispose();
        } else {
            Joiner joiner = Joiner.on(System.lineSeparator());
            String validationMessage = joiner.join(validationMessages);

            // add message to JTextArea
            JTextArea textArea = new JTextArea(validationMessage);
            // put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(contentPane, scrollPane, "Validation errors", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private QueryInput onValid() {
        // Populate QueryInput instance
        QueryInput queryInput = new QueryInput();

        String selectClause = selectTextField.getText();
        queryInput.setSelectClause(selectClause);
        String table = fromTextField.getText();
        queryInput.setTable(table);

        // gather equals params
        for (int i = 0; i < equalsParamsListModel.getFields().size(); i++) {
            queryInput.getEqualsParams().put(equalsParamsListModel.getFields().get(i), equalsParamsListModel.getParams().get(i));
        }

        // gather like params
        for (int i = 0; i < likeParamsListModel.getFields().size(); i++) {
            queryInput.getLikeParams().put(likeParamsListModel.getFields().get(i), likeParamsListModel.getParams().get(i));
        }

        // year params
        if (!fromYearTextField.getText().isEmpty() && !toYearTextField.getText().isEmpty()) {
            List<String> betweenParams = new ArrayList<String>();
            betweenParams.add(fromYearTextField.getText());
            betweenParams.add(toYearTextField.getText());
            queryInput.getBetweenParams().put(YEAR_FIELD, betweenParams);
        }

        if (!fromYearTextField.getText().isEmpty() && toYearTextField.getText().isEmpty()) {
            queryInput.getFromParams().put(YEAR_FIELD, fromYearTextField.getText());
        }

        if (fromYearTextField.getText().isEmpty() && !toYearTextField.getText().isEmpty()) {
            queryInput.getToParams().put(YEAR_FIELD, toYearTextField.getText());
        }

        // gather order params
        for (int i = 0; i < sortListModel.getFields().size(); i++) {
            queryInput.getOrderParams().put(sortListModel.getFields().get(i), sortListModel.getSorting().get(i));
        }

        return queryInput;
    }

    /**
     * Validate the user input.
     *
     * @return the list of validation messages.
     */
    private List<String> validateInput() {
        List<String> validationMessages = new ArrayList<String>();

        if (selectTextField.getText().isEmpty()) {
            validationMessages.add("Select text field is empty.");
        }

        if (fromTextField.getText().isEmpty()) {
            validationMessages.add("From text field is empty.");
        }

        if (!fromYearTextField.getText().isEmpty() && !isValidYear(fromYearTextField.getText())) {
            validationMessages.add("Please put a valid year in the 'from' field.");
        }

        if (!toYearTextField.getText().isEmpty() && !isValidYear(toYearTextField.getText())) {
            validationMessages.add("Please put a valid year in the 'to' field.");
        }

        return validationMessages;
    }

    /**
     * Check for Strings with only separators.
     *
     * @param paramString the parameter String
     * @return whether the parameter String is valid or not
     */
    private boolean isValidParamString(String paramString) {

        Iterable<String> result = Splitter.on(PARAM_SEPARATOR)
                .trimResults()
                .omitEmptyStrings()
                .split(paramString);

        return !Iterables.isEmpty(result);
    }

    /**
     * Check if the year input is valid.
     *
     * @return whether the year is valid or not.
     */
    private boolean isValidYear(String yearString) {
        boolean isValid = true;

        // trim String
        String trimmedYearString = yearString.trim();

        try {
            int year = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            isValid = false;
        }

        return isValid;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMinimumSize(new Dimension(700, 685));
        contentPane.setPreferredSize(new Dimension(800, 699));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(5, 5, 5, 5), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder("General"));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Select");
        panel5.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(51, 15), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("From");
        panel5.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(51, 15), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(354, 55), null, 0, false));
        selectTextField = new JTextField();
        panel6.add(selectTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromTextField = new JTextField();
        panel6.add(fromTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel3.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder("Equals parameters"));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panel10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        equalsLabel2 = new JLabel();
        equalsLabel2.setText("field = 'param' or field in ('param1', 'param2', ...)");
        panel10.add(equalsLabel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panel11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        equalsParamsTextField = new JTextField();
        panel11.add(equalsParamsTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        equalsFieldLabel = new JLabel();
        equalsFieldLabel.setText("Field");
        panel11.add(equalsFieldLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        equalsParametersLabel = new JLabel();
        equalsParametersLabel.setText("Parameters");
        panel11.add(equalsParametersLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        equalsFieldComboBox = new JComboBox();
        panel11.add(equalsFieldComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panel12, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        removeEqualsButton = new JButton();
        removeEqualsButton.setText("Remove");
        panel12.add(removeEqualsButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addEqualsButton = new JButton();
        addEqualsButton.setText("Add");
        panel12.add(addEqualsButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel13, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel13.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel13.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        equalsParamsList = new JList();
        equalsParamsList.setSelectionMode(0);
        scrollPane1.setViewportView(equalsParamsList);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel3.add(panel14, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel14.setBorder(BorderFactory.createTitledBorder("Like parameters"));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel14.add(panel15, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(panel16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        likeLabel2 = new JLabel();
        likeLabel2.setText("field like '%param1%' or field like '%param2%'...");
        panel17.add(likeLabel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel18, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        likeParamsTextField = new JTextField();
        panel18.add(likeParamsTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Field");
        panel18.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Parameters");
        panel18.add(label4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        likeFieldComboBox = new JComboBox();
        panel18.add(likeFieldComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel19, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        removeLikeButton = new JButton();
        removeLikeButton.setText("Remove");
        panel19.add(removeLikeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addLikeButton = new JButton();
        addLikeButton.setText("Add");
        panel19.add(addLikeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(panel20, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel20.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel20.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        likeParamsList = new JList();
        likeParamsList.setSelectionMode(0);
        scrollPane2.setViewportView(likeParamsList);
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 4, new Insets(5, 5, 5, 5), -1, -1));
        panel3.add(panel21, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel21.setBorder(BorderFactory.createTitledBorder("Publication year"));
        fromYearTextField = new JTextField();
        panel21.add(fromYearTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        toYearTextField = new JTextField();
        panel21.add(toYearTextField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromYearLabel = new JLabel();
        fromYearLabel.setText("From");
        panel21.add(fromYearLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toYearLabel = new JLabel();
        toYearLabel.setText("To");
        panel21.add(toYearLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(1, 2, new Insets(5, 5, 5, 5), -1, -1));
        panel3.add(panel22, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel22.setBorder(BorderFactory.createTitledBorder(null, "Order parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-16777216)));
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel22.add(panel23, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel24, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        orderFieldComboBox = new JComboBox();
        panel24.add(orderFieldComboBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sortingComboBox = new JComboBox();
        panel24.add(sortingComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel25 = new JPanel();
        panel25.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel25, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        removeOrderButton = new JButton();
        removeOrderButton.setText("Remove");
        panel25.add(removeOrderButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addOrderButton = new JButton();
        addOrderButton.setText("Add");
        panel25.add(addOrderButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel26 = new JPanel();
        panel26.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel22.add(panel26, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        orderParamsList = new JList();
        panel26.add(orderParamsList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
