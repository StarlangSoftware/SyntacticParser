package Annotation;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import DataCollector.DataCollector;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.ParseTree.TreeStructureEditorFrame;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class TreeSyntacticFrame extends TreeStructureEditorFrame {
    static final protected String ADDPARENT = "addparent";
    static final protected String EDITSYMBOL = "editsymbol";
    static final protected String DELETESYMBOL = "deletesymbol";
    static final protected String SPLITNODE = "splitnode";
    static final protected String DELETESUBTREE = "deletesubtree";

    /**
     * Constructor of the Syntactic annotation frame for parse trees. It reads the annotated tree bank and adds several
     * buttons such as add parent, edit symbol, split node, delete symbol, delete subtree.
     */
    public TreeSyntacticFrame(){
        TreeBankDrawable treeBank = new TreeBankDrawable(new File(TreeEditorPanel.treePath));
        this.setTitle("Annotation");
        JButton button;
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "addparent", ADDPARENT, "Add Parent");
        button.setVisible(true);
        toolBar.add(button);
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "edit", EDITSYMBOL, "Edit Symbol");
        button.setVisible(true);
        toolBar.add(button);
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "split", SPLITNODE, "Split Node");
        button.setVisible(true);
        toolBar.add(button);
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "delete", DELETESYMBOL, "Delete Symbol");
        button.setVisible(true);
        toolBar.add(button);
        button = new DrawingButton(DataCollector.class, this, "clear", DELETESUBTREE, "Delete Subtree");
        button.setVisible(true);
        toolBar.add(button);
        toolBar.addSeparator();
        JMenuItem itemAddParent = addMenuItem(treeMenu, "Add Parent", KeyStroke.getKeyStroke('p'));
        JMenuItem itemEditSymbol = addMenuItem(treeMenu, "Edit Symbol", KeyStroke.getKeyStroke('e'));
        JMenuItem itemDeleteSymbol = addMenuItem(treeMenu, "Delete Symbol", KeyStroke.getKeyStroke('l'));
        JMenuItem itemSplitNode = addMenuItem(treeMenu, "Split Node", KeyStroke.getKeyStroke('t'));
        JMenuItem itemDeleteSubtree = addMenuItem(treeMenu, "Delete Subtree", KeyStroke.getKeyStroke('b'));
        itemAddParent.addActionListener(e -> addParent());
        itemEditSymbol.addActionListener(e -> editSymbol());
        itemDeleteSymbol.addActionListener(e -> deleteSymbol());
        itemSplitNode.addActionListener(e -> splitNode());
        itemDeleteSubtree.addActionListener(e -> deleteSubtree());
        JMenuItem itemViewRules = addMenuItem(projectMenu, "View Rules", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        itemViewRules.addActionListener(e -> new ViewTreeSyntacticAnnotationFrame(treeBank, this));
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeSyntacticPanel(currentPath, rawFileName, ViewLayerType.TURKISH_WORD);
    }

    /**
     * Adds a new empty parent to the selected node.
     */
    protected void addParent(){
        TreeSyntacticPanel current = (TreeSyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.addParent();
        }
    }

    /**
     * Changes the symbol of the selected node.
     */
    protected void editSymbol(){
        TreeSyntacticPanel current = (TreeSyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.editSymbol();
        }
    }

    /**
     * Deletes the selected node.
     */
    protected void deleteSymbol(){
        TreeSyntacticPanel current = (TreeSyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.deleteSymbol();
        }
    }

    /**
     * Divides the selected node into multiple child nodes. The node should be a multiword expression such as 'ödü patladı'.
     */
    protected void splitNode(){
        TreeSyntacticPanel current = (TreeSyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.splitNode();
        }
    }

    /**
     * Deletes the subtree rooted at the selected node.
     */
    protected void deleteSubtree(){
        TreeSyntacticPanel current = (TreeSyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.deleteSubtree();
        }
    }

    /**
     * Calls corresponding functions for the buttons.
     * @param e Action event to be responded.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case ADDPARENT:
                addParent();
                break;
            case EDITSYMBOL:
                editSymbol();
                break;
            case DELETESYMBOL:
                deleteSymbol();
                break;
            case SPLITNODE:
                splitNode();
                break;
            case DELETESUBTREE:
                deleteSubtree();
                break;
        }
    }
}
