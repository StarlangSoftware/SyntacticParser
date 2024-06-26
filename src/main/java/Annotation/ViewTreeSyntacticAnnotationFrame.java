package Annotation;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.TreeBankDrawable;
import AnnotatedTree.WordNotExistsException;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.RowComparator2;
import Util.DrawingButton;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewTreeSyntacticAnnotationFrame extends JFrame implements ActionListener {
    protected ArrayList<ArrayList<String>> data;
    protected JTable dataTable;
    protected TreeBankDrawable treeBank;
    protected int COLOR_COLUMN_INDEX;

    protected static final String ID_SORT = "sortid";

    protected void updateGroupColors(){
        int groupCount = 0;
        data.get(0).set(COLOR_COLUMN_INDEX, "0");
        for (int i = 1; i < data.size(); i++){
            if (!data.get(i).get(1).equals(data.get(i - 1).get(1))){
                groupCount++;
            }
            data.get(i).set(COLOR_COLUMN_INDEX, "" + groupCount);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ID_SORT:
                data.sort(new RowComparator2(1, 0));
                updateGroupColors();
                JOptionPane.showMessageDialog(this, "Rules Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    public class CellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int groupCount = Integer.parseInt(data.get(row).get(COLOR_COLUMN_INDEX));
            if (groupCount % 2 == 0){
                c.setBackground(Color.WHITE);
            } else {
                c.setBackground(Color.LIGHT_GRAY);
            }
            if (isSelected){
                c.setBackground(Color.BLUE);
            }
            return c;
        }
    }

    private void addRule(ParseTreeDrawable parseTree, ParseNodeDrawable parseNode){
        if (parseNode.numberOfChildren() > 0) {
            StringBuilder rule = new StringBuilder(parseNode.getData() + " ->");
            StringBuilder sentenceString = new StringBuilder("<html>");
            for (int i = 0; i < parseNode.numberOfChildren(); i++) {
                ParseNodeDrawable child = (ParseNodeDrawable) parseNode.getChild(i);
                if (child.numberOfChildren() > 0){
                    rule.append(" ").append(child.getData());
                } else {
                    try {
                        rule.append(" ").append(child.getLayerInfo().getMorphologicalParseAt(0).getWord().getName());
                    } catch (LayerNotExistsException | WordNotExistsException e) {
                        rule.append(" ").append(child.getLayerData(ViewLayerType.TURKISH_WORD));
                    }
                }
                switch (i) {
                    case 0:
                        sentenceString.append(" <b><font color=\"red\">").append(child.toTurkishSentence()).append("</font></b>");
                        break;
                    case 1:
                        sentenceString.append(" <b><font color=\"blue\">").append(child.toTurkishSentence()).append("</font></b>");
                        break;
                    case 2:
                        sentenceString.append(" <b><font color=\"green\">").append(child.toTurkishSentence()).append("</font></b>");
                        break;
                    case 3:
                        sentenceString.append(" <b><font color=\"fuchsia\">").append(child.toTurkishSentence()).append("</font></b>");
                        break;
                    case 4:
                        sentenceString.append(" <b><font color=\"aqua\">").append(child.toTurkishSentence()).append("</font></b>");
                        break;
                    case 5:
                        sentenceString.append(" <b><font color=\"grey\">").append(child.toTurkishSentence()).append("</font></b>");
                        break;
                    case 6:
                        sentenceString.append(" <b><font color=\"pink\">").append(child.toTurkishSentence()).append("</font></b>");
                        break;
                    default:
                        sentenceString.append(" <b><font color=\"black\">").append(child.toTurkishSentence()).append("</font></b>");
                        break;
                }
            }
            sentenceString.append("</html>");
            ArrayList<String> row = new ArrayList<>();
            row.add(parseTree.getFileDescription().getRawFileName());
            row.add(rule.toString());
            row.add(sentenceString.toString());
            row.add("0");
            data.add(row);
            for (int i = 0; i < parseNode.numberOfChildren(); i++) {
                ParseNodeDrawable child = (ParseNodeDrawable) parseNode.getChild(i);
                addRule(parseTree, child);
            }
        }
    }

    /**
     * Constructs the data table. For every sentence, the columns are:
     * <ol>
     *     <li>Parse tree file name</li>
     *     <li>Rule for the tree</li>
     * </ol>
     * @param treeBank Annotated NER treebank
     */
    protected void prepareData(TreeBankDrawable treeBank){
        data = new ArrayList<>();
        for (int i = 0; i < treeBank.size(); i++){
            ParseTreeDrawable parseTree = treeBank.get(i);
            addRule(parseTree, (ParseNodeDrawable) parseTree.getRoot());
        }
    }

    public class DependencyTableDataModel extends AbstractTableModel {

        public int getColumnCount() {
            return COLOR_COLUMN_INDEX;
        }

        public int getRowCount() {
            return data.size();
        }

        public Class getColumnClass(int col) {
            return Object.class;
        }

        public Object getValueAt(int row, int col) {
            return data.get(row).get(col);
        }

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "FileName";
                case 1:
                    return "Rule";
                case 2:
                    return "Sentence";
                default:
                    return "";
            }
        }

    }

    /**
     * Constructs syntactic tree frame viewer. If the user double-clicks any row, the method automatically creates a
     * new panel showing associated parse tree.
     * @param treeBank Annotated parse tree
     * @param syntacticFrame Frame in which new panels will be created, when the user double-clicks a row.
     */
    public ViewTreeSyntacticAnnotationFrame(TreeBankDrawable treeBank, TreeSyntacticFrame syntacticFrame){
        this.treeBank = treeBank;
        COLOR_COLUMN_INDEX = 3;
        JToolBar toolBar = new JToolBar("ToolBox");
        JButton idSort = new DrawingButton(ViewTreeSyntacticAnnotationFrame.class, this, "sortnumbers", ID_SORT, "");
        toolBar.add(idSort);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        prepareData(treeBank);
        dataTable = new JTable(new DependencyTableDataModel());
        dataTable.getColumnModel().getColumn(0).setMinWidth(150);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(1).setMinWidth(200);
        dataTable.getColumnModel().getColumn(2).setMinWidth(300);
        dataTable.setDefaultRenderer(Object.class, new CellRenderer());
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2){
                    int row = dataTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        String fileName = data.get(row).get(0);
                        syntacticFrame.addPanelToFrame(new TreeSyntacticPanel(TreeEditorPanel.treePath, fileName, ViewLayerType.TURKISH_WORD), fileName);
                    }
                }
            }
        });
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
    }


}
