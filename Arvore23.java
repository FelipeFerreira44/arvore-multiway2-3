class No23 {
    int[] chaves;
    No23[] filhos;
    int numChaves;
    int numFilhos;
    boolean ehFolha;

    public No23(boolean ehFolha) {
        this.chaves = new int[3]; 
        this.filhos = new No23[4]; 
        this.numChaves = 0;
        this.numFilhos = 0;
        this.ehFolha = ehFolha;
    }

    public void adicionarChave(int chave) {
        
        int i = numChaves - 1;
        while (i >= 0 && chaves[i] > chave) {
            chaves[i + 1] = chaves[i];
            i--;
        }
        chaves[i + 1] = chave;
        numChaves++;
    }

    public void adicionarFilho(No23 filho) {
        
        int chaveFilho = (filho.numChaves > 0) ? filho.chaves[0] : Integer.MAX_VALUE;
        int i = numFilhos - 1;
        while (i >= 0) {
            int chaveAtual = (filhos[i].numChaves > 0) ? filhos[i].chaves[0] : Integer.MAX_VALUE;
            if (chaveAtual <= chaveFilho) break;
            filhos[i + 1] = filhos[i];
            i--;
        }
        filhos[i + 1] = filho;
        numFilhos++;
    }

    public boolean estaCheio() {
        return numChaves == 3; 
    }

    public int encontrarIndiceFilho(int valor) {
        for (int i = 0; i < numChaves; i++) {
            if (valor < chaves[i]) {
                return i;
            }
        }
        return numChaves;
    }

    public void removerChave(int indice) {
        for (int i = indice; i < numChaves - 1; i++) {
            chaves[i] = chaves[i + 1];
        }
        numChaves--;
    }

    public void removerFilho(int indice) {
        for (int i = indice; i < numFilhos - 1; i++) {
            filhos[i] = filhos[i + 1];
        }
        numFilhos--;
    }

    @Override
    public String toString() {
        String s = "Chaves: [";
        for (int i = 0; i < numChaves; i++) {
            s += chaves[i];
            if (i < numChaves - 1) s += ", ";
        }
        s += "]" + (ehFolha ? " (Folha)" : " (Interno)");
        return s;
    }
}

public class Arvore23 {
    private No23 raiz;

    public Arvore23() {
        this.raiz = null;
    }

    public boolean buscar(int valor) {
        if (raiz == null) {
            System.out.println("Árvore vazia. " + valor + " não encontrado.");
            return false;
        }
        return buscar(raiz, valor);
    }

    private boolean buscar(No23 no, int valor) {
        for (int i = 0; i < no.numChaves; i++) {
            if (no.chaves[i] == valor) {
                System.out.println("Valor " + valor + " encontrado no nó: " + no.toString());
                return true;
            }
        }

        if (no.ehFolha) {
            System.out.println("Valor " + valor + " não encontrado (chegou na folha).");
            return false;
        }

        int indiceFilho = no.encontrarIndiceFilho(valor);
        return buscar(no.filhos[indiceFilho], valor);
    }

    public void inserir(int valor) {
        System.out.println("\nInserindo: " + valor);
        if (raiz == null) {
            raiz = new No23(true);
            raiz.adicionarChave(valor);
            return;
        }

        No23 novaRaiz = inserirRecursivo(raiz, valor);
        if (novaRaiz != null) {
            raiz = novaRaiz;
        }
    }

    private No23 inserirRecursivo(No23 no, int valor) {
        if (no.ehFolha) {
            no.adicionarChave(valor);

            if (no.estaCheio()) {
                return dividirNo(no);
            }
            return null;
        } else {
            int indiceFilho = no.encontrarIndiceFilho(valor);
            No23 filho = no.filhos[indiceFilho];

            No23 noPromovido = inserirRecursivo(filho, valor);

            if (noPromovido != null) {
                no.adicionarChave(noPromovido.chaves[0]);

                no.removerFilho(indiceFilho);
                for (int i = 0; i < noPromovido.numFilhos; i++) {
                    no.adicionarFilho(noPromovido.filhos[i]);
                }

                if (no.estaCheio()) {
                    return dividirNo(no);
                }
            }
            return null;
        }
    }

    private No23 dividirNo(No23 no) {
        No23 esquerda = new No23(no.ehFolha);
        No23 direita = new No23(no.ehFolha);
        No23 pai = new No23(false);

        esquerda.adicionarChave(no.chaves[0]);
        direita.adicionarChave(no.chaves[2]);
        pai.adicionarChave(no.chaves[1]);

        if (!no.ehFolha) {
            esquerda.filhos[0] = no.filhos[0];
            esquerda.filhos[1] = no.filhos[1];
            esquerda.numFilhos = 2;

            direita.filhos[0] = no.filhos[2];
            direita.filhos[1] = no.filhos[3];
            direita.numFilhos = 2;
        }

        pai.filhos[0] = esquerda;
        pai.filhos[1] = direita;
        pai.numFilhos = 2;

        return pai;
    }

    public void remover(int valor) {
        System.out.println("\nRemovendo: " + valor);
        if (raiz == null) {
            System.out.println("Árvore vazia, nada para remover.");
            return;
        }

        removerRecursivo(raiz, valor);

        if (raiz.numChaves == 0 && raiz.numFilhos > 0) {
            raiz = raiz.filhos[0];
        }
    }

    private boolean removerRecursivo(No23 no, int valor) {
        int indiceChave = -1;
        for (int i = 0; i < no.numChaves; i++) {
            if (no.chaves[i] == valor) {
                indiceChave = i;
                break;
            }
        }

        if (no.ehFolha) {
            if (indiceChave != -1) {
                no.removerChave(indiceChave);
                System.out.println("Chave " + valor + " removida da folha.");
                return no.numChaves < 1;
            } else {
                System.out.println("Chave " + valor + " não encontrada na folha.");
                return false;
            }
        } else {
            if (indiceChave != -1) {
                No23 noPredecessor = encontrarPredecessor(no.filhos[indiceChave]);
                int valorPredecessor = noPredecessor.chaves[noPredecessor.numChaves - 1];

                no.chaves[indiceChave] = valorPredecessor;

                return removerRecursivo(no.filhos[indiceChave], valorPredecessor);
            } else {
                int indiceFilho = no.encontrarIndiceFilho(valor);
                boolean precisaBalanceamento = removerRecursivo(no.filhos[indiceFilho], valor);

                if (precisaBalanceamento) {
                    return tratarUnderflow(no, indiceFilho);
                }
                return false;
            }
        }
    }

    private No23 encontrarPredecessor(No23 no) {
        if (no.ehFolha) {
            return no;
        }
        return encontrarPredecessor(no.filhos[no.numFilhos - 1]);
    }

    private boolean tratarUnderflow(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos[indiceFilho];

        if (indiceFilho > 0) {
            No23 irmaoEsquerdo = pai.filhos[indiceFilho - 1];
            if (irmaoEsquerdo.numChaves > 1) {
                redistribuirEsquerda(pai, indiceFilho);
                return false;
            }
        }

        if (indiceFilho < pai.numFilhos - 1) {
            No23 irmaoDireito = pai.filhos[indiceFilho + 1];
            if (irmaoDireito.numChaves > 1) {
                redistribuirDireita(pai, indiceFilho);
                return false;
            }
        }

        if (indiceFilho > 0) {
            return mergeComIrmaoEsquerdo(pai, indiceFilho);
        } else {
            return mergeComIrmaoDireito(pai, indiceFilho);
        }
    }

    private void redistribuirEsquerda(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos[indiceFilho];
        No23 irmaoEsquerdo = pai.filhos[indiceFilho - 1];

        filho.adicionarChave(pai.chaves[indiceFilho - 1]);

        pai.chaves[indiceFilho - 1] = irmaoEsquerdo.chaves[irmaoEsquerdo.numChaves - 1];
        irmaoEsquerdo.removerChave(irmaoEsquerdo.numChaves - 1);

        if (!filho.ehFolha) {
            for (int i = filho.numFilhos; i > 0; i--) {
                filho.filhos[i] = filho.filhos[i - 1];
            }
            filho.filhos[0] = irmaoEsquerdo.filhos[irmaoEsquerdo.numFilhos - 1];
            filho.numFilhos++;
            irmaoEsquerdo.removerFilho(irmaoEsquerdo.numFilhos - 1);
        }
    }

    private void redistribuirDireita(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos[indiceFilho];
        No23 irmaoDireito = pai.filhos[indiceFilho + 1];

        filho.adicionarChave(pai.chaves[indiceFilho]);

        pai.chaves[indiceFilho] = irmaoDireito.chaves[0];
        irmaoDireito.removerChave(0);

        if (!filho.ehFolha) {
            filho.filhos[filho.numFilhos] = irmaoDireito.filhos[0];
            filho.numFilhos++;
            irmaoDireito.removerFilho(0);
        }
    }

    private boolean mergeComIrmaoEsquerdo(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos[indiceFilho];
        No23 irmaoEsquerdo = pai.filhos[indiceFilho - 1];

        irmaoEsquerdo.adicionarChave(pai.chaves[indiceFilho - 1]);
        pai.removerChave(indiceFilho - 1);

        for (int i = 0; i < filho.numChaves; i++) {
            irmaoEsquerdo.adicionarChave(filho.chaves[i]);
        }
        for (int i = 0; i < filho.numFilhos; i++) {
            irmaoEsquerdo.adicionarFilho(filho.filhos[i]);
        }

        pai.removerFilho(indiceFilho);

        return pai.numChaves == 0;
    }

    private boolean mergeComIrmaoDireito(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos[indiceFilho];
        No23 irmaoDireito = pai.filhos[indiceFilho + 1];

        filho.adicionarChave(pai.chaves[indiceFilho]);
        pai.removerChave(indiceFilho);

        for (int i = 0; i < irmaoDireito.numChaves; i++) {
            filho.adicionarChave(irmaoDireito.chaves[i]);
        }
        for (int i = 0; i < irmaoDireito.numFilhos; i++) {
            filho.adicionarFilho(irmaoDireito.filhos[i]);
        }

        pai.removerFilho(indiceFilho + 1);

        return pai.numChaves == 0;
    }

    public void imprimirArvore() {
        System.out.println("\n--- Árvore 2-3 ---");
        if (raiz == null) {
            System.out.println("Árvore vazia.");
            return;
        }
        imprimirArvore(raiz, 0);
        System.out.println("------------------");
    }

    private void imprimirArvore(No23 no, int nivel) {
        for (int i = 0; i < nivel; i++) {
            System.out.print("  ");
        }
        System.out.println(no);

        if (!no.ehFolha) {
            for (int i = 0; i < no.numFilhos; i++) {
                imprimirArvore(no.filhos[i], nivel + 1);
            }
        }
    }

    public static void main(String[] args) {
        Arvore23 arvore = new Arvore23();

        System.out.println("=== TESTE DA ÁRVORE 2-3 ===");

        int[] valores = {50, 30, 70, 20, 40, 60, 80};

        for (int valor : valores) {
            arvore.inserir(valor);
            arvore.imprimirArvore();
        }

        System.out.println("\n--- Teste de Busca ---");
        arvore.buscar(30);
        arvore.buscar(100);
        arvore.buscar(40);

        System.out.println("\n--- Teste de Remoção ---");
        arvore.remover(40);
        arvore.imprimirArvore();
        arvore.remover(20);
        arvore.imprimirArvore();
    }
}
