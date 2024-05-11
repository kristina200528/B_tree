import java.util.Random;

public class BTree {
    private int T; //минимальная степень дерева
    public static int op = 0;
    private Node root;

    public BTree(int t) {
        T = t;
        root = new Node();
        root.n = 0;
        root.leaf = true;
    }

    // Создаем узел
    public class Node {
        int n; //текущее количество ключей
        int key[] = new int[2 * T - 1]; //массив ключей
        Node child[] = new Node[2 * T]; //массив ссылок на дочерние узлы
        boolean leaf = true; //флаг, является ли узел листовым

        public int Find(int k) {
            for (int i = 0; i < this.n; i++) {
                op++;
                if (this.key[i] == k) {
                    return i;
                }
            }
            return -1;
        }
    }

    // Поиск ключа
    private Node Search(Node x, int key) {
        int i = 0;
        if (x == null)
            return x;
        for (i = 0; i < x.n; i++) {
            op++;
            if (key < x.key[i]) {
                break;
            }
            if (key == x.key[i]) {
                return x;
            }
        }
        if (x.leaf) {
            return null;
        } else {
            return Search(x.child[i], key);
        }
    }

    // Разбиение узла
    private void Split(Node x, int pos, Node y) {
        Node z = new Node();
        z.leaf = y.leaf;
        z.n = T - 1;
        for (int j = 0; j < T - 1; j++) {
            z.key[j] = y.key[j + T];
            op++;
        }
        if (!y.leaf) {
            for (int j = 0; j < T; j++) {
                z.child[j] = y.child[j + T];
                op++;
            }
        }
        y.n = T - 1;
        for (int j = x.n; j >= pos + 1; j--) {
            x.child[j + 1] = x.child[j];
            op++;
        }
        x.child[pos + 1] = z;
        for (int j = x.n - 1; j >= pos; j--) {
            x.key[j + 1] = x.key[j];
            op++;
        }
        x.key[pos] = y.key[T - 1];
        x.n = x.n + 1;
    }

    // Вставка значения
    public void Insert(int key) {
        Node r = root;
        op++;
        if (r.n == 2 * T - 1) {
            Node s = new Node();
            root = s;
            s.leaf = false;
            s.n = 0;
            s.child[0] = r;
            Split(s, 0, r);
            insertValue(s, key);
        } else {
            insertValue(r, key);
        }
    }

    // Вставка узла
    private void insertValue(Node x, int k) {
        if (x.leaf) {
            int i = 0;
            for (i = x.n - 1; i >= 0 && k < x.key[i]; i--) {
                x.key[i + 1] = x.key[i];
                op++;
            }
            x.key[i + 1] = k;
            x.n = x.n + 1;
        } else {
            int i = 0;
            for (i = x.n - 1; i >= 0 && k < x.key[i]; i--) {
                op++;
            }
            i++;
            Node tmp = x.child[i];
            if (tmp.n == 2 * T - 1) {
                Split(x, i, tmp);
                if (k > x.key[i]) {
                    i++;
                }
            }
            insertValue(x.child[i], k);
        }
    }

    private void Remove(Node x, int key) {
        int pos = x.Find(key);
        if (pos != -1) {
            if (x.leaf) {
                int i = 0;
                for (i = 0; i < x.n && x.key[i] != key; i++) {
                    op++;
                }
                for (; i < x.n; i++) {
                    op++;
                    if (i != 2 * T - 2) {
                        x.key[i] = x.key[i + 1];
                    }
                }
                x.n--;
                return;
            }
            if (!x.leaf) {
                Node pred = x.child[pos];
                int predKey = 0;
                if (pred.n >= T) {
                    for (; ; ) {
                        op++;
                        if (pred.leaf) {
                            predKey = pred.key[pred.n - 1];
                            break;
                        } else {
                            pred = pred.child[pred.n];
                        }
                    }
                    Remove(pred, predKey);
                    x.key[pos] = predKey;
                    return;
                }
                Node nextNode = x.child[pos + 1];
                if (nextNode.n >= T) {
                    int nextKey = nextNode.key[0];
                    if (!nextNode.leaf) {
                        nextNode = nextNode.child[0];
                        for (; ; ) {
                            op++;
                            if (nextNode.leaf) {
                                nextKey = nextNode.key[nextNode.n - 1];
                                break;
                            } else {
                                nextNode = nextNode.child[nextNode.n];
                            }
                        }
                    }
                    Remove(nextNode, nextKey);
                    x.key[pos] = nextKey;
                    return;
                }
                int temp = pred.n + 1;
                pred.key[pred.n++] = x.key[pos];
                for (int i = 0, j = pred.n; i < nextNode.n; i++) {
                    op++;
                    pred.key[j++] = nextNode.key[i];
                    pred.n++;
                }
                for (int i = 0; i < nextNode.n + 1; i++) {
                    op++;
                    pred.child[temp++] = nextNode.child[i];
                }
                x.child[pos] = pred;
                for (int i = pos; i < x.n; i++) {
                    op++;
                    if (i != 2 * T - 2) {
                        x.key[i] = x.key[i + 1];
                    }
                }
                for (int i = pos + 1; i < x.n + 1; i++) {
                    op++;
                    if (i != 2 * T - 1) {
                        x.child[i] = x.child[i + 1];
                    }
                }
                x.n--;
                if (x.n == 0) {
                    if (x == root) {
                        root = x.child[0];
                    }
                    x = x.child[0];
                }
                Remove(pred, key);
            }
        }
        else {
            for (pos = 0; pos < x.n; pos++) {
                op++;
                if (x.key[pos] > key) {
                    break;
                }
            }
            Node tmp = x.child[pos];
            if (tmp.n >= T) {
                Remove(tmp, key);
                return;
            }
            if (true) {
                Node nb = null;
                int devider = -1;
                if (pos != x.n && x.child[pos + 1].n >= T) {
                    devider = x.key[pos];
                    nb = x.child[pos + 1];
                    x.key[pos] = nb.key[0];
                    tmp.key[tmp.n++] = devider;
                    tmp.child[tmp.n] = nb.child[0];
                    for (int i = 1; i < nb.n; i++) {
                        op++;
                        nb.key[i - 1] = nb.key[i];
                    }
                    for (int i = 1; i <= nb.n; i++) {
                        op++;
                        nb.child[i - 1] = nb.child[i];
                    }
                    nb.n--;
                    Remove(tmp, key);
                } else if (pos != 0 && x.child[pos - 1].n >= T) {
                    devider = x.key[pos - 1];
                    nb = x.child[pos - 1];
                    x.key[pos - 1] = nb.key[nb.n - 1];
                    Node child = nb.child[nb.n];
                    nb.n--;
                    for (int i = tmp.n; i > 0; i--) {
                        op++;
                        tmp.key[i] = tmp.key[i - 1];
                    }
                    tmp.key[0] = devider;
                    for (int i = tmp.n + 1; i > 0; i--) {
                        op++;
                        tmp.child[i] = tmp.child[i - 1];
                    }
                    tmp.child[0] = child;
                    tmp.n++;
                    Remove(tmp, key);
                } else {
                    Node lt = null;
                    Node rt = null;
                    boolean last = false;
                    if (pos != x.n) {
                        devider = x.key[pos];
                        lt = x.child[pos];
                        rt = x.child[pos + 1];
                    } else {
                        devider = x.key[pos - 1];
                        rt = x.child[pos];
                        lt = x.child[pos - 1];
                        last = true;
                        pos--;
                    }
                    for (int i = pos; i < x.n - 1; i++) {
                        op++;
                        x.key[i] = x.key[i + 1];
                    }
                    for (int i = pos + 1; i < x.n; i++) {
                        op++;
                        x.child[i] = x.child[i + 1];
                    }
                    x.n--;
                    lt.key[lt.n++] = devider;
                    for (int i = 0, j = lt.n; i < rt.n + 1; i++, j++) {
                        op++;
                        if (i < rt.n) {
                            lt.key[j] = rt.key[i];
                        }
                        lt.child[j] = rt.child[i];
                    }
                    lt.n += rt.n;
                    if (x.n == 0) {
                        if (x == root) {
                            root = x.child[0];
                        }
                        x = x.child[0];
                    }
                    Remove(lt, key);
                }
            }
        }
    }

    public void Remove(int key) {
        Node x = Search(root, key);
        if (x == null) {
            return;
        }
        Remove(root, key);
    }

    public void Show() {
        Show(root);
    }

    // Вывод на экран
    private void Show(Node x) {
        assert (x == null);
        for (int i = 0; i < x.n; i++) {
            System.out.print(x.key[i] + " ");
        }
        if (!x.leaf) {
            for (int i = 0; i < x.n + 1; i++) {
                Show(x.child[i]);
            }
        }
    }

    public static void main(String[] args) {
        BTree b = new BTree(3);

        //Генерация массива из случайной последовательности 10000 целых чисел
        int[] array = new int[10000];
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(100000) + 1;
        }

        // Случайный выбор 1000 элементов из массива
        int[] selectedNumbers = new int[1000];
        for (int i = 0; i < 1000; i++) {
            int randomIndex = random.nextInt(array.length);
            selectedNumbers[i] = array[randomIndex];
        }

        //Поэлементное добавление 10.000 чисел
        for (int i = 0; i < array.length; i++) {
            op = 0;
            System.out.println("Добавление:");
            long startTime = System.nanoTime();
            b.Insert(array[i]);
            System.out.println(System.nanoTime()-startTime+"        "+op);
        }

        //Поиск случайных 100 элементов в структуре
        for (int i = 0; i < 100; i++) {
            op = 0;
            System.out.println("Поиск:");
            long startTime = System.nanoTime();
            b.Search(b.root, selectedNumbers[i]);
            System.out.println(System.nanoTime()-startTime+"        "+op);
        }

        //Удаление случайных 1000 элементов из массива
        for (int i = 0; i < 1000; i++) {
            op = 0;
            System.out.println("Удаление:");
            long startTime = System.nanoTime();
            b.Remove(selectedNumbers[i]);
            System.out.println(System.nanoTime()-startTime+"        "+op);
        }

    }
}