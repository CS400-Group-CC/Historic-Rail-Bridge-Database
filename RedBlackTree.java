// --== CS400 File Header Information ==--
// Name: Colby Brown
// Email: csbrown7@wisc.edu
// Team: CC
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: 
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Stack;


/**
 * Red-Black Tree implementation with a Node inner class for representing the nodes of the tree.
 * Currently, this implements a Binary Search Tree that we will turn into a red black tree by
 * modifying the insert functionality. In this activity, we will start with implementing rotations
 * for the binary search tree insert algorithm. You can use this class' insert method to build a
 * regular binary search tree, and its toString method to display a level-order traversal of the
 * tree.
 */
public class RedBlackTree<T extends Comparable<T>> implements SortedCollectionInterface<T> {

  /**
   * This class represents a node holding a single value within a binary tree the parent, left, and
   * right child references are always maintained.
   */
  protected static class Node<T> {
    public T data;
    public Node<T> parent; // null for root node
    public Node<T> leftChild;
    public Node<T> rightChild;
    public boolean isBlack;

    public Node(T data) {
      this.data = data;
      this.isBlack = false;
    }

    /**
     * @return true when this node has a parent and is the left child of that parent, otherwise
     *         return false
     */
    public boolean isLeftChild() {
      return parent != null && parent.leftChild == this;
    }

    /**
     * This method performs a level order traversal of the tree rooted at the current node. The
     * string representations of each data value within this tree are assembled into a comma
     * separated string within brackets (similar to many implementations of java.util.Collection).
     * Note that the Node's implementation of toString generates a level order traversal. The
     * toString of the RedBlackTree class below produces an inorder traversal of the nodes / values
     * of the tree. This method will be helpful as a helper for the debugging and testing of your
     * rotation implementation.
     * 
     * @return string containing the values of this tree in level order
     */
    @Override
    public String toString() {
      String output = "[";
      LinkedList<Node<T>> q = new LinkedList<>();
      q.add(this);
      while (!q.isEmpty()) {
        Node<T> next = q.removeFirst();
        if (next.leftChild != null)
          q.add(next.leftChild);
        if (next.rightChild != null)
          q.add(next.rightChild);
        output += next.data.toString();
        if (!q.isEmpty())
          output += ", ";
      }
      return output + "]";
    }
  }

  protected Node<T> root; // reference to root node of tree, null when empty
  protected int size = 0; // the number of values in the tree

  
  /**
   * Finds an item within the tree
   * 
   * @param input
   * @return matching item
   * @throws NoSuchElementException
   */
  public T findItem(T input) throws NoSuchElementException {
    Node<T> inputNode = new Node(input);
    
    T foundItem = findHelper(root, inputNode);
    
    if (foundItem == null) {
      throw new NoSuchElementException("Element not found");
    }
    
    return foundItem;
  }
  
  /**
   * Recursive helper for finding a matching item
   * 
   * @param currentNode
   * @param input
   * @return matching item or null if not found
   */
  private T findHelper(Node<T> currentNode, Node<T> input) {
    
    int compare = currentNode.data.compareTo(input.data);
    
    //Found
    if (compare == 0) {
      return currentNode.data;
    } 
    //No children
    else if (currentNode.leftChild == null && currentNode.rightChild == null) {
      return null;
    } 
    //Input is less than current
    else if (compare > 0) {
      if (currentNode.leftChild != null)
        return findHelper(currentNode.leftChild, input);
      else
        return null;
    } 
    //Input is greater than current
    else {
      if (currentNode.rightChild != null)
        return findHelper(currentNode.rightChild, input);
      else
        return null;
    }
    
  }
  
  /**
   * Performs a naive insertion into a binary search tree: adding the input data value to a new node
   * in a leaf position within the tree. After this insertion, no attempt is made to restructure or
   * balance the tree. This tree will not hold null references, nor duplicate data values.
   * 
   * @param data to be added into this binary search tree
   * @return true if the value was inserted, false if not
   * @throws NullPointerException     when the provided data argument is null
   * @throws IllegalArgumentException when the newNode and subtree contain equal data references
   */
  @Override
  public boolean insert(T data) throws NullPointerException, IllegalArgumentException {
    // null references cannot be stored within this tree
    if (data == null)
      throw new NullPointerException("This RedBlackTree cannot store null references.");

    Node<T> newNode = new Node<>(data);
    if (root == null) {
      root = newNode;
      size++;
      root.isBlack = true; //Root is always black
      return true;
    } // add first node to an empty tree
    else {
      boolean returnValue = insertHelper(newNode, root); // recursively insert into subtree
      if (returnValue)
        size++;
      else
        throw new IllegalArgumentException("This RedBlackTree already contains that value.");
      root.isBlack = true; //Root is always black
      return returnValue;
    }
  }

  /**
   * Recursive helper method to find the subtree with a null reference in the position that the
   * newNode should be inserted, and then extend this tree by the newNode in that position.
   * 
   * @param newNode is the new node that is being added to this tree
   * @param subtree is the reference to a node within this tree which the newNode should be inserted
   *                as a descenedent beneath
   * @return true is the value was inserted in subtree, false if not
   */
  private boolean insertHelper(Node<T> newNode, Node<T> subtree) {
    int compare = newNode.data.compareTo(subtree.data);
    // do not allow duplicate values to be stored within this tree
    if (compare == 0)
      return false;

    // store newNode within left subtree of subtree
    else if (compare < 0) {
      if (subtree.leftChild == null) { // left subtree empty, add here
        subtree.leftChild = newNode;
        newNode.parent = subtree;
        enforceRBTreePropertiesAfterInsert(newNode); //Fix violations
        return true;
        // otherwise continue recursive search for location to insert
      } else
        return insertHelper(newNode, subtree.leftChild);
    }

    // store newNode within the right subtree of subtree
    else {
      if (subtree.rightChild == null) { // right subtree empty, add here
        subtree.rightChild = newNode;
        newNode.parent = subtree;
        enforceRBTreePropertiesAfterInsert(newNode); //Fix violations
        return true;
        // otherwise continue recursive search for location to insert
      } else
        return insertHelper(newNode, subtree.rightChild);
    }
  }
  
  /**
   * Enforces all red black tree properties and takes care of any violations
   * 
   * @param node - newly added node
   */
  private void enforceRBTreePropertiesAfterInsert(Node<T> node) {
    
    //Node is root
    if (node.parent == null) {
      return;
    }
    //New nodes parent is black, no violation (Case 0)
    else if (node.parent.isBlack || node.isBlack) {
      enforceRBTreePropertiesAfterInsert(node.parent);
    }
    //New nodes parent is red and uncle is red (Case 1)
    else if (node.parent.isBlack == false && getSiblingIsBlack(node.parent) == false) {
      Node<T> parent = node.parent;
      Node<T> grandparent = node.parent.parent;
      Node<T> uncle = getUncle(node);
      
      //Recolor
      parent.isBlack = true;
      uncle.isBlack = true;
      grandparent.isBlack = false;
      
      enforceRBTreePropertiesAfterInsert(node.parent);
    }
    //New nodes parent is red and uncle is black on same side (Case 2)
    else if (node.parent.isBlack == false && getSiblingIsBlack(node.parent) && uncleOnSameSide(node)) {
      
      Node<T> newChild = node.parent;
      
      rotate(node, node.parent);
      
      enforceRBTreePropertiesAfterInsert(newChild);
    }
    //New nodes parent is red and uncle is black on different side (Case 3)
    else if (node.parent.isBlack == false && getSiblingIsBlack(node.parent) && !uncleOnSameSide(node)) {
      
      Node<T> parent = node.parent;
      Node<T> grandparent = node.parent.parent;
      
      rotate(node.parent, node.parent.parent);
      
      //Recolor
      parent.isBlack = true;
      grandparent.isBlack = false;
      
      enforceRBTreePropertiesAfterInsert(node.parent);
    }
    
    
  }
  
  /**
   * Gets sibling of node
   * 
   * @param node
   * @return sibling node
   */
  private Node<T> getSibling(Node<T> node) {
    if (node.parent.rightChild == node) {
      return node.parent.leftChild;
    } else if (node.parent.leftChild == node) {
      return node.parent.rightChild;
    } else {
      return null;
    }
  }
  
  /**
   * Checks if sibling is black
   * 
   * @param node
   * @return sibling node color
   */
  private boolean getSiblingIsBlack(Node<T> node) {
    if (node.parent.rightChild == node) {
      Node<T> sibling = node.parent.leftChild;
      if (sibling != null) {
        return sibling.isBlack;
      } else {
        return true;
      }
    } else if (node.parent.leftChild == node) {
      Node<T> sibling = node.parent.rightChild;
      if (sibling != null) {
        return sibling.isBlack;
      }
      else {
        return true;
      }
    } else {
      return true;
    }
  }
  
  /**
   * Checks if node and uncle are on the same side
   * 
   * @param node
   * @return true if they are, false otherwise
   */
  private boolean uncleOnSameSide(Node<T> node) {
    if (node.parent.leftChild == node) {
      if (node.parent.parent.leftChild != node.parent) {
        return true;
      } else {
        return false;
      }
    } else if (node.parent.rightChild == node){
      if (node.parent.parent.rightChild != node.parent) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  /**
   * Gets uncle of current node
   * 
   * @param node
   * @return uncle of node
   */
  private Node<T> getUncle(Node<T> node) {
    Node<T> parent = node.parent;
    Node<T> grandparent = parent.parent;
    
    if (grandparent.leftChild == parent) {
      return grandparent.rightChild;
    } else {
      return grandparent.leftChild;
    }
  }

  /**
   * Performs the rotation operation on the provided nodes within this tree. When the provided child
   * is a leftChild of the provided parent, this method will perform a right rotation. When the
   * provided child is a rightChild of the provided parent, this method will perform a left
   * rotation. When the provided nodes are not related in one of these ways, this method will throw
   * an IllegalArgumentException.
   * 
   * @param child  is the node being rotated from child to parent position (between these two node
   *               arguments)
   * @param parent is the node being rotated from parent to child position (between these two node
   *               arguments)
   * @throws IllegalArgumentException when the provided child and parent node references are not
   *                                  initially (pre-rotation) related that way
   */
  private void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {
    // TODO: Implement this method.
    if (child.isLeftChild() && child.parent == parent) {
      // Is left child, perform right rotation
      parent.leftChild = child.rightChild;
      if (child.rightChild != null)
        child.rightChild.parent = parent;
      child.parent = parent.parent;
      if (parent.parent == null)
        root = child;
      if (parent.isLeftChild())
        parent.parent.leftChild = child;
      else if (!parent.isLeftChild() && parent.parent != null)
        parent.parent.rightChild = child;
      child.rightChild = parent;
      parent.parent = child;
    } else if (!child.isLeftChild() && child.parent == parent) {
      // Is right child, perform left rotation
      parent.rightChild = child.leftChild;
      if (child.leftChild != null)
        child.leftChild.parent = parent;
      child.parent = parent.parent;
      if (parent.parent == null)
        root = child;
      if (parent.isLeftChild())
        parent.parent.leftChild = child;
      else if (!parent.isLeftChild() && parent.parent != null)
        parent.parent.rightChild = child;
      child.leftChild = parent;
      parent.parent = child;
    } else {
      // Is not a child or this parent, throw exception
      throw new IllegalArgumentException(
          "The provided child and parent references are not related");
    }
  }

  /**
   * Get the size of the tree (its number of nodes).
   * 
   * @return the number of nodes in the tree
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * Method to check if the tree is empty (does not contain any node).
   * 
   * @return true of this.size() return 0, false if this.size() > 0
   */
  @Override
  public boolean isEmpty() {
    return this.size() == 0;
  }

  /**
   * Checks whether the tree contains the value *data*.
   * 
   * @param data the data value to test for
   * @return true if *data* is in the tree, false if it is not in the tree
   */
  @Override
  public boolean contains(T data) {
    // null references will not be stored within this tree
    if (data == null)
      throw new NullPointerException("This RedBlackTree cannot store null references.");
    return this.containsHelper(data, root);
  }

  /**
   * Recursive helper method that recurses through the tree and looks for the value *data*.
   * 
   * @param data    the data value to look for
   * @param subtree the subtree to search through
   * @return true of the value is in the subtree, false if not
   */
  private boolean containsHelper(T data, Node<T> subtree) {
    if (subtree == null) {
      // we are at a null child, value is not in tree
      return false;
    } else {
      int compare = data.compareTo(subtree.data);
      if (compare < 0) {
        // go left in the tree
        return containsHelper(data, subtree.leftChild);
      } else if (compare > 0) {
        // go right in the tree
        return containsHelper(data, subtree.rightChild);
      } else {
        // we found it :)
        return true;
      }
    }
  }

  /**
   * Returns an iterator over the values in in-order (sorted) order.
   * 
   * @return iterator object that traverses the tree in in-order sequence
   */
  @Override
  public Iterator<T> iterator() {
    // use an anonymous class here that implements the Iterator interface
    // we create a new on-off object of this class everytime the iterator
    // method is called
    return new Iterator<T>() {
      // a stack and current reference store the progress of the traversal
      // so that we can return one value at a time with the Iterator
      Stack<Node<T>> stack = null;
      Node<T> current = root;

      /**
       * The next method is called for each value in the traversal sequence. It returns one value at
       * a time.
       * 
       * @return next value in the sequence of the traversal
       * @throws NoSuchElementException if there is no more elements in the sequence
       */
      public T next() {
        // if stack == null, we need to initialize the stack and current element
        if (stack == null) {
          stack = new Stack<Node<T>>();
          current = root;
        }
        // go left as far as possible in the sub tree we are in until we hit a null
        // leaf (current is null), pushing all the nodes we fund on our way onto the
        // stack to process later
        while (current != null) {
          stack.push(current);
          current = current.leftChild;
        }
        // as long as the stack is not empty, we haven't finished the traversal yet;
        // take the next element from the stack and return it, then start to step down
        // its right subtree (set its right sub tree to current)
        if (!stack.isEmpty()) {
          Node<T> processedNode = stack.pop();
          current = processedNode.rightChild;
          return processedNode.data;
        } else {
          // if the stack is empty, we are done with our traversal
          throw new NoSuchElementException("There are no more elements in the tree");
        }

      }

      /**
       * Returns a boolean that indicates if the iterator has more elements (true), or if the
       * traversal has finished (false)
       * 
       * @return boolean indicating whether there are more elements / steps for the traversal
       */
      public boolean hasNext() {
        // return true if we either still have a current reference, or the stack
        // is not empty yet
        return !(current == null && (stack == null || stack.isEmpty()));
      }

    };
  }

  /**
   * This method performs an inorder traversal of the tree. The string representations of each data
   * value within this tree are assembled into a comma separated string within brackets (similar to
   * many implementations of java.util.Collection, like java.util.ArrayList, LinkedList, etc). Note
   * that this RedBlackTree class implementation of toString generates an inorder traversal. The
   * toString of the Node class class above produces a level order traversal of the nodes / values
   * of the tree.
   * 
   * @return string containing the ordered values of this tree (in-order traversal)
   */
  @Override
  public String toString() {
    // use the inorder Iterator that we get by calling the iterator method above
    // to generate a string of all values of the tree in (ordered) in-order
    // traversal sequence
    Iterator<T> treeNodeIterator = this.iterator();
    StringBuffer sb = new StringBuffer();
    sb.append("[ ");
    if (treeNodeIterator.hasNext())
      sb.append(treeNodeIterator.next());
    while (treeNodeIterator.hasNext()) {
      T data = treeNodeIterator.next();
      sb.append(", ");
      sb.append(data.toString());
    }
    sb.append(" ]");
    return sb.toString();
  }

}
