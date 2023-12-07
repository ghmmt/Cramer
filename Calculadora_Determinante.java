import java.util.Scanner;

public class Calculadora_Determinante {

    final static Scanner ler = new Scanner(System.in);

    public static void main(String[] args) {
        int N = -1;
        while (ler.hasNext() && N != 0) { //Caso acabe o arquivo ou o usuario digite 0, o programa acaba
            System.out.println("Digite a ordem da matriz: (Caso queira fechar o programa, digite 0)");
            N = ler.nextInt();

            if (N == 0) {  //caso N seja igual a 0 o programa acaba
                break;
            } else if (N < 0) { //caso N seja menor que 0, o valor será invalido, logo ignoramos a leitura
                System.out.println("Ordem da matriz inválida.");
                System.out.println();
                continue;
            }

            double matriz[][] = lerMatriz(N); //leitura da matriz
            double naturais[] = lerNaturais(N);

            String resultados[] = cramer(N, matriz, naturais);
            
            imprimirResultados(resultados);
        }

        System.out.println();
        System.out.println("Programa finalizado.");
    }

    public static double[][] lerMatriz(int N) {
        double matriz[][] = new double[N][N]; 

        System.out.println();
        System.out.println("Digite a matriz principal:");
        for (int i = 0; i < N; i++) {
            System.out.printf("Linha %d:\n", i + 1);
            for (int j = 0; j < N; j++) {
                matriz[i][j] = ler.nextDouble();
            }
        }

        return matriz;
    }

    public static double[] lerNaturais(int N) {
        double naturais[] = new double[N];

        System.out.println();
        System.out.println("Agora, digite os naturais. (Por exemplo, na equação '3x + 2y = 12', o natural é 12)");
        for (int i = 0; i < N; i++) {
            System.out.printf("Digite o natural da equação numero %d:\n", i + 1);
            naturais[i] = ler.nextDouble();
        }

        return naturais;
    }

    public static String[] cramer(int N, double[][] matriz, double[] naturais) {
        double determinante = calcularDeterminante(N, matriz);
        double determinantes[] = new double[N];

        for (int i = 0; i < N; i++) {
            double matrizNova[][] = alterarColuna(matriz, naturais, i);
            determinantes[i] = calcularDeterminante(N, matrizNova);
        }

        return obterResultados(N, determinante, determinantes);
    }

    public static double calcularDeterminante(int N, double[][] matriz) {
        double resultado = 0;
        switch (N) { //caso de 1 a 3 são resolvidos por sarrus, 4 pra frente, por laplace & sarrus
            //o determinante de uma matriz 1x1 sempre será o único elemento da matriz,
            case 1: 
                resultado = matriz[0][0];
                break;
            case 2: //o determinante de uma matriz 2x2 sempre será a subtração entre a multiplicação dos valores cruzados
                resultado = (matriz[0][0] * matriz[1][1]) - (matriz[0][1] * matriz[1][0]);
                break;
            case 3: //algoritmo de sarrus
                resultado = sarrus(matriz);
                break;
            default: //algoritmo de laplace
                resultado = laplace(matriz);
                break;
        }

        return resultado;
    }

    public static String[] obterResultados(int N, double determinante, double[] determinantes) {
        String resultados[] = new String[N];
        for (int i = 0; i < resultados.length; i++) {
            int div[] = {(int) determinantes[i], (int) determinante};
            simplificarFracao(div);
            resultados[i] = div[0] + "/" + div[1];
        }

        return resultados;
    }

    public static double[][] alterarColuna(double[][] matriz, double[] naturais, int coluna) {
        double matrizNova[][] = copiarMatriz(matriz);
        
        for (int i = 0; i < matrizNova.length; i++) {
            matrizNova[i][coluna] = naturais[i];
        }

        return matrizNova;
    }

    public static double[][] copiarMatriz(double[][] x) {
        double y[][] = new double[x.length][x[0].length];

        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y[0].length; j++) {
                y[i][j] = x[i][j];
            }
        }

        return y;
    }

    public static void realizarOperacao(int[] vetor) {
        //todas as variaveis crescerão +1, e se seus valores forem maiores do que a quantidade de variáveis
        //são resetados (volta ao índice 0)
        for (int i = 0; i < vetor.length; i++) {
            vetor[i] = (vetor[i] + 1) % vetor.length;
        }
    }

    public static int[] inicializarVariaveisIda(int N) {
        //a evolução dos índices na ida é marcada pelo crescimento das variáveis de forma crescente
        int variaveis[] = new int[N];

        for (int i = 0; i < N; i++) {
            variaveis[i] = i;
        }

        return variaveis;
    }

    public static int[] inicializarVariaveisVolta(int N) {
        //na volta, a evolução é marcada com a constante sendo índice 0, e em seguida os índices em ordem decrescente
        //ex: 0, 3, 2, 1
        int variaveis[] = new int[N];
        variaveis[0] = 0;

        for (int i = N - 1, j = 1; i >= 1; i--, j++) {
            variaveis[j] = i;
        }

        return variaveis;
    }

    public static double sarrus(double[][] matriz) {
        //multiplicação pelas diagonais, por isso as variáveis são inicializadas das formas tratadas nos métodos anteriores
        int N = matriz.length;
        double ida = 0;
        for (int variaveisIda[] = inicializarVariaveisIda(N), i = 0; i < N; i++, realizarOperacao(variaveisIda)) {
            double aux = 1;
            for (int j = 0; j < N; j++) {
                aux *= matriz[variaveisIda[j]][j];
            }
            ida += aux;
        }

        double volta = 0;
        for (int variaveisVolta[] = inicializarVariaveisVolta(N), i = 0; i < N; i++, realizarOperacao(variaveisVolta)) {
            double aux = 1;
            for (int j = 0; j < N; j++) {
                aux *= matriz[variaveisVolta[j]][j];
            }
            volta += aux;
        }

        return ida - volta;
    }

    public static void imprimirResultados(String[] resultados) {
        System.out.println();
        System.out.println("O valor das variáveis são:");
        for (int i = 0; i < resultados.length; i++) {
            System.out.printf("%c = %s\n", (char) i + 97, resultados[i]);
        }
    }

    public static double laplace(double[][] matriz) {
        //expansão de matriz em várias submatrizes, até o momento que cheguemos numa matriz 3x3
        //quando obtida as matrizes 3x3, é calculado o determinante destes
        //cálculo da expansão de laplace: N! / 3!
        if (matriz.length == 3) return sarrus(matriz);
        else {
            double determinante = 0;
            for (int j = 0; j < matriz.length; j++) {
                double matrizAuxiliar[][] = montarMatrizMenor(matriz, j);
                determinante += matriz[0][j] * formulaCofator(0, j, laplace(matrizAuxiliar));
            }

            return determinante;
        }
    }

    public static double formulaCofator(int i, int j, double determinante) {
        //auxiliar do calculo de laplace (Aij * Cij), Cij = (-1)^(i+j)
        return Math.pow(-1, i + j + 2) * determinante;
    }

    public static double[][] montarMatrizMenor(double[][] matriz, int coluna) {
        //formação das submatrizes de laplace. para facilitar a configuração do algoritmo, sempre sendo na linha 0
        //apenas alterando a coluna
        int N = matriz.length - 1;
        double matrizMenor[][] = new double[N][N];

        for (int i = 0, l = 0; i < N; i++, l++) {
            if (i == 0) l++;
            for (int j = 0, c = 0; j < N; j++, c++) {
                if (j == coluna) c++;
                matrizMenor[i][j] = matriz[l][c];
            }
        }

        return matrizMenor;
    }

    public static void simplificarFracao(int[] div) {
        int i = 1;
        int n1 = div[0];
        int n2 = div[1];
        while (true) {
            if (divOuN(n1, i) && divOuN(n2, i)) {
                div[0] = n1 / i; div[1] = n2 / i;
                
                if ((divOuN(n1, n2))) {
                    div[0] = n1/n2; div[1] = 1;
                }
            }
            i++;
            
            if (i > n2) {
                if (div[0] < 0 && div[1] < 0) {
                    div[0] = div[0] * -1;
                    div[1] = div[1] * -1;
                } 
                
                return;
            }
        }
    }

    public static boolean divOuN(int x, int div) {
        return (int) x / div - (float) x / div == 0;
    }
}