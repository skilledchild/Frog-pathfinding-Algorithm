/**
 * Implementation of a unique priority queue using arrays.
 * Each element has a unique value and an associated priority.
 * Lower priority values indicate higher priority (closer to the front).
 * @param <T> the type of elements stored in the queue
 */
public class ArrayUniquePriorityQueue<T> implements UniquePriorityQueueADT<T> {
    
    private T[] queue;         // Array to store the elements
    private double[] priority; // Array to store the priorities of elements
    private int count;         // Number of elements currently in the queue
    
    /**
     * Constructor initializes the queue with capacity of 10.
     */
    public ArrayUniquePriorityQueue() {
        count = 0;
        queue = (T[]) new Object[10];    // Create generic array with type cast
        priority = new double[10];
    }
    
    /**
     * Adds a new element with given priority to the queue.
     * If the element already exists, it will NOT be added.
     * Elements are sorted by priority in ascending order.
     * @param data the element to add
     * @param prio the priority associated with the element
     */
    public void add(T data, double prio) {
        if (!contains(data)) {  // Only add if element is not already present

            // Expand arrays if full
            if (size() == queue.length) {
                T[] tempQue = (T[]) new Object[queue.length + 5];
                for (int i = 0; i < size(); i++) {
                    tempQue[i] = queue[i];
                }
                queue = tempQue;

                double[] tempDou = new double[priority.length + 5];
                for (int i = 0; i < size(); i++) {
                    tempDou[i] = priority[i];
                }
                priority = tempDou;
            }

            if (isEmpty()) {  // If empty, add element at position 0
                priority[0] = prio;
                queue[0] = data;
            } else {
                // Find the right position to insert based on priority
                for (int i = 0; i < size(); i++) {
                    if (prio < priority[i]) {  // New element has higher priority (lower value)
                        // Shift elements and priorities to the right to make space
                        for (int j = size(); j > i; j--) {
                            priority[j] = priority[j - 1];
                        }
                        for (int j = size(); j > i; j--) {
                            queue[j] = queue[j - 1];
                        }
                        // Insert new element
                        priority[i] = prio;
                        queue[i] = data;
                        break;
                    } else if (prio == priority[i] && prio != priority[i + 1]) {
                        // Insert element right after an element with the same priority
                        for (int j = size(); j > i; j--) {
                            priority[j] = priority[j - 1];
                        }
                        for (int j = size(); j > i; j--) {
                            queue[j] = queue[j - 1];
                        }
                        priority[i + 1] = prio;
                        queue[i + 1] = data;
                        break;
                    } else if (prio > priority[size() - 1]) {
                        // New element has the lowest priority, append at the end
                        priority[size()] = prio;
                        queue[size()] = data;
                        break;
                    }
                }
            }
            count++;  // Increase element count
        }
    }
    
    /**
     * Checks whether the queue contains a specified element.
     * @param data the element to check
     * @return true if element exists, false otherwise
     */
    public boolean contains(T data) {
        for (int i = 0; i < size(); i++) {
            if (data.equals(queue[i])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the element with the highest priority without removing it.
     * @return the element with the highest priority
     * @throws CollectionException if the queue is empty
     */
    public T peek() throws CollectionException {
        if (isEmpty()) throw new CollectionException("PQ is empty");
        return queue[0];
    }
    
    /**
     * Removes and returns the element with the highest priority.
     * @return the removed element
     * @throws CollectionException if the queue is empty
     */
    public T removeMin() throws CollectionException {
        if (isEmpty()) throw new CollectionException("PQ is empty");
        
        T temp = queue[0];  // Store element to return
        
        // Shift elements to the left to remove the first element
        for (int i = 0; i < size() - 1; i++) {
            queue[i] = queue[i + 1];
        }
        queue[size() - 1] = null;

        // Shift priorities similarly
        for (int i = 0; i < size() - 1; i++) {
            priority[i] = priority[i + 1];
        }
        priority[size() - 1] = 0.0;
        count--;  // Decrement element count
        return temp;
    }
    
    /**
     * Updates the priority of a given element.
     * Removes the element and re-inserts it with the new priority to maintain order.
     * @param data the element to update
     * @param newPrio the new priority
     * @throws CollectionException if the element is not found in the queue
     */
    public void updatePriority(T data, double newPrio) throws CollectionException {
        if (!contains(data)) throw new CollectionException("Item not found in PQ");

        for (int i = 0; i < size(); i++) {
            if (data.equals(queue[i])) {  // Find element index
                // Remove element by shifting left
                for (int j = i; j < size() - 1; j++) {
                    queue[j] = queue[j + 1];
                }
                queue[size() - 1] = null;

                // Shift priorities similarly
                for (int j = i; j < size() - 1; j++) {
                    priority[j] = priority[j + 1];
                }
                priority[size() - 1] = 0.0;
                count--;
                add(data, newPrio);  // Re-insert with new priority
                break;
            }
        }
    }
    
    /**
     * Checks if the queue is empty.
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return count == 0;
    }
    
    /**
     * Returns the number of elements in the queue.
     * @return current size of the queue
     */
    public int size() {
        return count;
    }
    
    /**
     * Returns the current capacity of the internal arrays.
     * @return length of the underlying arrays
     */
    public int getLength() {
        return queue.length;
    }
    
    /**
     * Returns a string representation of the queue showing elements and their priorities.
     * @return string listing elements with their priorities
     */
    public String toString() {
        if (isEmpty()) return "The PQ is empty";
        String str = queue[0] + " [" + priority[0] + "]";
        
        for (int i = 1; i < size(); i++) {
            str = str + ", " + queue[i] + " [" + priority[i] + "]";
        }
        
        return str;
    }
}
