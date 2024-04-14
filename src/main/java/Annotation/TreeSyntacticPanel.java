package Annotation;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.*;
import DataCollector.ParseTree.TreeStructureEditorPanel;
import ParseTree.ParseNode;
import ParseTree.Symbol;
import Util.RectAngle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class TreeSyntacticPanel extends TreeStructureEditorPanel {
    private ParseNodeDrawable draggedNode = null;
    private ParseNodeDrawable fromNode = null;
    private boolean dragged = false;
    private int draggedIndex = -1;
    private final JTextField editText;

    /**
     * Constructor for the syntactic editor panel for a parse tree. It also adds the edit text listener.
     *
     * @param path      The absolute path of the annotated parse tree.
     * @param fileName  The raw file name of the annotated parse tree.
     * @param viewLayer -
     */
    public TreeSyntacticPanel(String path, String fileName, ViewLayerType viewLayer) {
        super(path, fileName, viewLayer);
        editText = new JTextField();
        editText.setHorizontalAlignment(JTextField.CENTER);
        editText.setVisible(false);
        editText.addActionListener(actionEvent -> {
            isEditing = false;
            if (!editText.getText().contains("(") && !editText.getText().contains(")") && !editText.getText().contains("{") && !editText.getText().contains("}")) {
                TreeEditAction action;
                if (editableNode.isLeaf()) {
                    action = new LayerAction(TreeSyntacticPanel.this, editableNode.getLayerInfo(), editText.getText(), ViewLayerType.TURKISH_WORD);
                } else {
                    action = new EditSymbolAction(TreeSyntacticPanel.this, editableNode, new Symbol(editText.getText()));
                }
                actionList.add(action);
                action.execute();
            }
            editText.setVisible(false);
            editableNode.setEditable(false);
            repaint();
        });
        add(editText);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Adds a new empty parent to the given node.
     */
    public void addParent() {
        if (editableNode != null && editableNode.numberOfChildren() > 0) {
            AddParentAction action = new AddParentAction(this, editableNode);
            action.execute();
            actionList.add(action);
            editableNode.setEditable(false);
            repaint();
        }
    }

    /**
     * Changes the symbol of the selected node.
     */
    public void editSymbol() {
        if (editableNode != null) {
            isEditing = true;
            if (editableNode.isLeaf()) {
                editText.setText(editableNode.getLayerData(ViewLayerType.TURKISH_WORD));
            } else {
                editText.setText(editableNode.getData().getName());
            }
            RectAngle rect = editableNode.getArea();
            editText.setBounds(rect.getX() - 20, rect.getY() - 4, rect.getWidth() + 40, rect.getHeight() + 8);
            editText.setVisible(true);
            editText.requestFocus();
        }
    }

    /**
     * Deletes the selected node.
     */
    public void deleteSymbol() {
        if (editableNode != null && (editableNode.numberOfChildren() != 1 || !editableNode.getChild(0).isLeaf())) {
            DeleteNodeAction action = new DeleteNodeAction(this, editableNode);
            action.execute();
            actionList.add(action);
            editableNode.setEditable(false);
            repaint();
        }
    }

    /**
     * Deletes the subtree rooted at the selected node.
     */
    public void deleteSubtree() {
        if (editableNode != null && !editableNode.isLeaf()) {
            DeleteSubtreeAction action = new DeleteSubtreeAction(this, editableNode);
            action.execute();
            actionList.add(action);
            editableNode.setEditable(false);
            repaint();
        }
    }

    /**
     * Divides the selected node into multiple child nodes. The node should be a multiword expression such as 'ödü patladı'.
     */
    public void splitNode() {
        if (editableNode != null && editableNode.numberOfChildren() == 0) {
            SplitNodeAction action = new SplitNodeAction(this, currentTree, editableNode);
            action.execute();
            actionList.add(action);
            editableNode.setEditable(false);
            repaint();
        }
    }

    /**
     * If the user presses mouse button, fromNode will be determined.
     *
     * @param mouseEvent Mouse event to be handled
     */
    public void mousePressed(MouseEvent mouseEvent) {
        ParseNode node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node == previousNode && previousNode != null) {
            fromNode = previousNode;
        }
    }

    /**
     * If the user ends a drag event, which is understood by draggedNode not null, a move subtree action is executed.
     *
     * @param mouseEvent Mouse event to be handled
     */
    public void mouseReleased(MouseEvent mouseEvent) {
        if (fromNode != null) {
            fromNode.setSelected(false);
            fromNode.setEditable(false);
        }
        if (draggedNode != null) {
            draggedNode.setDragged(false);
            draggedNode.setSelected(false);
            draggedNode.setEditable(false);
        }
        if (fromNode != null && draggedNode != null && dragged) {
            MoveSubtreeAction action = new MoveSubtreeAction(this, currentTree, fromNode, draggedNode, draggedIndex);
            action.execute();
            actionList.add(action);
        }
        fromNode = null;
        draggedNode = null;
        dragged = false;
        this.repaint();
    }

    /**
     * Starts a possible mouse drag event.
     *
     * @param mouseEvent Mouse event to be handled
     */
    public void mouseDragged(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        dragged = true;
        if (node != null && node != previousNode && node.numberOfChildren() > 0 && !node.getChild(0).isLeaf() && !fromNode.isDescendant(node)) {
            draggedNode = node;
            draggedIndex = (int) (((draggedNode.numberOfChildren() + 1) * (mouseEvent.getX() - node.getArea().getX())) / (node.getArea().getWidth() + 0.0));
            draggedNode.setDragged(true, draggedIndex);
            this.repaint();
        } else {
            if (node == null && draggedNode != null) {
                draggedNode.setDragged(false);
                draggedNode = null;
                this.repaint();
            }
        }
    }

    /**
     * When the mouse is moved, the previousNode can be changed, if the mouse is on top of a node.
     *
     * @param mouseEvent Mouse move event to handle.
     */
    public void mouseMoved(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null && node != previousNode && !dragged && !isEditing) {
            if (previousNode != null)
                previousNode.setSelected(false);
            node.setSelected(true);
            previousNode = node;
            this.repaint();
        } else {
            if (node == null && previousNode != null && !dragged && !isEditing) {
                previousNode.setSelected(false);
                previousNode = null;
                this.repaint();
            }
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null) {
            if (editableNode != null) {
                editableNode.setEditable(false);
            }
            editableNode = node;
            editableNode.setEditable(true);
            editText.setVisible(false);
            isEditing = false;
            this.repaint();
            this.setFocusable(true);
        }
    }

    /**
     * The size of the string displayed. If it is a leaf node, it returns the size of the word. Otherwise, it returns
     * the size of the symbol in the node.
     *
     * @param parseNode Parse node
     * @param g         Graphics on which tree will be drawn.
     * @return Size of the string displayed.
     */
    protected int getStringSize(ParseNodeDrawable parseNode, Graphics g) {
        if (parseNode.numberOfChildren() == 0) {
            return g.getFontMetrics().stringWidth(parseNode.getLayerData(ViewLayerType.TURKISH_WORD));
        } else {
            return g.getFontMetrics().stringWidth(parseNode.getData().getName());
        }
    }

    /**
     * If the node is a leaf node, it draws the word. Otherwise, it draws the node symbol.
     *
     * @param parseNode Parse Node
     * @param g         Graphics on which symbol is drawn.
     * @param x         x coordinate
     * @param y         y coordinate
     */
    protected void drawString(ParseNodeDrawable parseNode, Graphics g, int x, int y) {
        if (parseNode.numberOfChildren() == 0) {
            g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
        } else {
            g.drawString(parseNode.getData().getName(), x, y);
        }
    }

    /**
     * Sets the size of the enclosing area of the parse node (for selecting, editing etc.).
     *
     * @param parseNode  Parse Node
     * @param x          x coordinate of the center of the node.
     * @param y          y coordinate of the center of the node.
     * @param stringSize Size of the string in terms of pixels.
     */
    protected void setArea(ParseNodeDrawable parseNode, int x, int y, int stringSize) {
        parseNode.setArea(x - 5, y - 15, stringSize + 10, 20);
    }

}
