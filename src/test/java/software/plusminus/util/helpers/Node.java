package software.plusminus.util.helpers;

import lombok.Getter;
import lombok.Setter;

/**
 * Mutable graph node used to build cyclic and acyclic object graphs
 * for {@code ObjectUtils} traversal tests.
 */
@Getter
@Setter
public class Node {

    private String name;
    private Node left;
    private Node right;

    public Node(String name) {
        this.name = name;
    }

}
