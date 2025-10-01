import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class No23 {
    List<Integer> chaves;
    List<No23> filhos;
    boolean ehFolha;

    public No23(boolean ehFolha) {
        this.chaves = new ArrayList<>();
        this.filhos = new ArrayList<>();
        this.ehFolha = ehFolha;
    }

    public void adicionarChave(int chave) {
        this.chaves.add(chave);
        Collections.sort(this.chaves);
    }

    public void adicionarFilho(No23 filho) {
        this.filhos.add(filho);
        this.filhos.sort((n1, n2) -> {
            if (n1.chaves.isEmpty() || n2.chaves.isEmpty()) return 0;
            return Integer.compare(n1.chaves.get(0), n2.chaves.get(0));
        });
    }

    public boolean estaCheio() {
        return chaves.size() == 2;
    }

    public int encontrarIndiceFilho(int valor) {
        for (int i = 0; i < chaves.size(); i++) {
            if (valor < chaves.get(i)) {
                return i;
            }
        }
        return chaves.size();
    }

    @Override
    public String toString() {
        return "Chaves: " + chaves.toString() + (ehFolha ? " (Folha)" : " (Interno)");
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
        for (int chave : no.chaves) {
            if (chave == valor) {
                System.out.println("Valor " + valor + " encontrado no nó: " + no.chaves);
                return true;
            }
        }

        if (no.ehFolha) {
            System.out.println("Valor " + valor + " não encontrado (chegou na folha).");
            return false;
        }

        int indiceFilho = no.encontrarIndiceFilho(valor);
        return buscar(no.filhos.get(indiceFilho), valor);
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
            
            if (no.chaves.size() == 3) {
                return dividirNo(no);
            }
            return null;
        } else {
            int indiceFilho = no.encontrarIndiceFilho(valor);
            No23 filho = no.filhos.get(indiceFilho);
            
            No23 noPromovido = inserirRecursivo(filho, valor);
            
            if (noPromovido != null) {
                no.adicionarChave(noPromovido.chaves.get(0));
                
                no.filhos.remove(indiceFilho);
                for (No23 novoFilho : noPromovido.filhos) {
                    no.adicionarFilho(novoFilho);
                }
                
                if (no.chaves.size() == 3) {
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
        
        esquerda.adicionarChave(no.chaves.get(0));
        direita.adicionarChave(no.chaves.get(2));
        pai.adicionarChave(no.chaves.get(1));
        
        if (!no.ehFolha) {
            esquerda.filhos.add(no.filhos.get(0));
            esquerda.filhos.add(no.filhos.get(1));
            direita.filhos.add(no.filhos.get(2));
            direita.filhos.add(no.filhos.get(3));
        }
        
        pai.filhos.add(esquerda);
        pai.filhos.add(direita);
        
        return pai;
    }

    public void remover(int valor) {
        System.out.println("\nRemovendo: " + valor);
        if (raiz == null) {
            System.out.println("Árvore vazia, nada para remover.");
            return;
        }

        removerRecursivo(raiz, valor);
        
        if (raiz.chaves.isEmpty() && !raiz.filhos.isEmpty()) {
            raiz = raiz.filhos.get(0);
        }
    }

    private boolean removerRecursivo(No23 no, int valor) {
        int indiceChave = -1;
        for (int i = 0; i < no.chaves.size(); i++) {
            if (no.chaves.get(i) == valor) {
                indiceChave = i;
                break;
            }
        }

        if (no.ehFolha) {
            if (indiceChave != -1) {
                no.chaves.remove(indiceChave);
                System.out.println("Chave " + valor + " removida da folha.");
                return no.chaves.size() < 1;
            } else {
                System.out.println("Chave " + valor + " não encontrada na folha.");
                return false;
            }
        } else {
            if (indiceChave != -1) {
                No23 noPredecessor = encontrarPredecessor(no.filhos.get(indiceChave));
                int valorPredecessor = noPredecessor.chaves.get(noPredecessor.chaves.size() - 1);
                
                no.chaves.set(indiceChave, valorPredecessor);
                
                return removerRecursivo(no.filhos.get(indiceChave), valorPredecessor);
            } else {
                int indiceFilho = no.encontrarIndiceFilho(valor);
                boolean precisaBalanceamento = removerRecursivo(no.filhos.get(indiceFilho), valor);
                
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
        return encontrarPredecessor(no.filhos.get(no.filhos.size() - 1));
    }

    private boolean tratarUnderflow(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos.get(indiceFilho);
        
        if (indiceFilho > 0) {
            No23 irmaoEsquerdo = pai.filhos.get(indiceFilho - 1);
            if (irmaoEsquerdo.chaves.size() > 1) {
                redistribuirEsquerda(pai, indiceFilho);
                return false;
            }
        }
        
        if (indiceFilho < pai.filhos.size() - 1) {
            No23 irmaoDireito = pai.filhos.get(indiceFilho + 1);
            if (irmaoDireito.chaves.size() > 1) {
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
        No23 filho = pai.filhos.get(indiceFilho);
        No23 irmaoEsquerdo = pai.filhos.get(indiceFilho - 1);
        
        filho.adicionarChave(pai.chaves.get(indiceFilho - 1));
        
        pai.chaves.set(indiceFilho - 1, irmaoEsquerdo.chaves.remove(irmaoEsquerdo.chaves.size() - 1));
        
        if (!filho.ehFolha) {
            filho.filhos.add(0, irmaoEsquerdo.filhos.remove(irmaoEsquerdo.filhos.size() - 1));
        }
    }

    private void redistribuirDireita(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos.get(indiceFilho);
        No23 irmaoDireito = pai.filhos.get(indiceFilho + 1);
        
        filho.adicionarChave(pai.chaves.get(indiceFilho));
        
        pai.chaves.set(indiceFilho, irmaoDireito.chaves.remove(0));
        
        if (!filho.ehFolha) {
            filho.filhos.add(irmaoDireito.filhos.remove(0));
        }
    }

    private boolean mergeComIrmaoEsquerdo(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos.get(indiceFilho);
        No23 irmaoEsquerdo = pai.filhos.get(indiceFilho - 1);
        
        irmaoEsquerdo.adicionarChave(pai.chaves.remove(indiceFilho - 1));
        
        irmaoEsquerdo.chaves.addAll(filho.chaves);
        irmaoEsquerdo.filhos.addAll(filho.filhos);
        
        pai.filhos.remove(indiceFilho);
        
        return pai.chaves.isEmpty();
    }

    private boolean mergeComIrmaoDireito(No23 pai, int indiceFilho) {
        No23 filho = pai.filhos.get(indiceFilho);
        No23 irmaoDireito = pai.filhos.get(indiceFilho + 1);
        
        filho.adicionarChave(pai.chaves.remove(indiceFilho));
        
        filho.chaves.addAll(irmaoDireito.chaves);
        filho.filhos.addAll(irmaoDireito.filhos);
        
        pai.filhos.remove(indiceFilho + 1);
        
        return pai.chaves.isEmpty();
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
            for (No23 filho : no.filhos) {
                imprimirArvore(filho, nivel + 1);
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
