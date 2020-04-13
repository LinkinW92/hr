package my.linkin.collection;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Auther: chunhui.wu
 * @Date: 2020/3/12 11:04
 * @Description:
 */
public class SkipList {

    private static Node[] floors;

    public void add(int k) {
        int level = determine();
        Node[] temp = floors;
        // 需要新层时
        if (floors == null || floors.length < level + 1) {
            temp = copy(level);
        }
        List<Node> list = new ArrayList<>();
        for (int j = level; j >= 0; j--) {
            Node n = new Node(k), m = temp[j];
            list.add(n);
            if (m == null) {
                temp[j] = n;
            } else {
                Node pre = m;
                while (m != null && m.getValue() < k) {
                    pre = m;
                    m = m.next;
                }
                if (m != null && m == temp[j]) {
                    temp[j] = n;
                    n.next = m;
                } else {
                    n.next = m;
                    pre.next = n;
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).down = i + 1 > list.size() - 1 ? null : list.get(i + 1);
        }
        floors = temp;
        System.out.println("-----新增节点:" + k + "-------");
        print();
    }

    public void remove(int k) {
        if (floors == null || floors.length == 0) {
            return;
        }
        int f = floors.length - 1;
        Node m = floors[f], pre;
        while (m != null && f >= 0) {
            pre = m;
            while (m != null && m.getValue() < k) {
                pre = m;
                m = m.next;
            }
            if (m != null && m.getValue() == k) {
                if (pre == m) {
                    floors[f] = m.next;
                    if (f == 0) break;
                    m = floors[--f];
                    continue;
                }
                pre.next = m.next;
                m = m.down;
            } else {
                if (m != null && m.getValue() > k) {
                    if (f == 0) break;
                    m = floors[--f];
                } else {
                    m = pre.down;
                    --f;
                }
            }
        }
        System.out.println("-----删除节点:" + k + "-------");
        print();
    }

    public boolean checkExist(int k) {
        return false;
    }

    private Node[] copy(int level) {
        Node[] temp = new Node[level + 1];
        if (floors == null) {
            return temp;
        }
        for (int i = 0; i < floors.length; i++) {
            temp[i] = floors[i];
        }
        return temp;
    }

    private void print() {
        if (floors == null || floors.length == 0) {
            return;
        }
        for (int i = 0; i < floors.length; i++) {
            Node n = floors[i];
            while (n != null) {
                System.out.print(n.getValue() + ",");
                n = n.next;
            }
            System.out.println();
        }
    }

    /**
     * 投掷硬币决定层数， 层数越高概率越低
     */
    private int determine() {
        int k = 0;
        while (Math.random() < 0.5d) k++;
        return k;
    }


    @Data
    public static class Node {
        private int value;
        private Node next; // point to the right next node;
        private Node down; // point to the down next node

        public Node(int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        SkipList list = new SkipList();
        Set<Integer> s = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            s.add((int) (Math.random() * 1000));
        }
        for (Integer integer : s) {
            list.add(integer);
        }
        System.out.println("添加元素完成,共：" + s.size());
        s.stream().skip(10).limit(10).forEach(list::remove);
    }
}
