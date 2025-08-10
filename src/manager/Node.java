package manager;

import task.Task;

public class Node {
    private Node previous;
    private Task data;
    private Node next;

    public Node(Task data) {
        this.data = data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Task getData() {
        return data;
    }

    public void setData(Task data) {
        this.data = data;
    }
}
