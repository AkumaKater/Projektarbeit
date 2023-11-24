package FFNetwork;
import java.text.DecimalFormat;
import java.util.Arrays;

import MNISTReader.MNISTHTML;
import MNISTReader.MnistBuffer;
import MNISTReader.MnistMatrix;

public class Main {

    public static void main(String[] args) throws Exception{
        ConfigLoader config = ConfigLoader.getConfig();
        int splitIndex = config.getSplitIndex(); // 60.000
        int TrainingCycles = config.getTrainingCycles();
        int BatchSize = config.getBatchSize();
        DecimalFormat df = new DecimalFormat("0.00");

        MnistBuffer mBuffer = new MnistBuffer();
        mBuffer.loadMNIST();
        mBuffer.splitData(splitIndex);

        Activation.setActivation(config.getValue("Activation"));

        NeuralNetwork nn = new NeuralNetwork(config.getLearnRate(), config.getLayers());

        for(int i=0; i<(splitIndex*TrainingCycles/BatchSize); i++){
            MnistMatrix[] mnistMatrix = mBuffer.getBatch(BatchSize);
            nn.learn(mnistMatrix);
            System.out.print('\r');
            System.out.print(df.format((double) i / (splitIndex*TrainingCycles/BatchSize-1) * 100) + "%");
        }

        String accTrain = getAcuracy(nn, mBuffer.getTrainingsData());
        String accTest = getAcuracy(nn, mBuffer.getTestData());

        MNISTHTML html = new MNISTHTML()
        .h1("Visualisierung der Netzwerk Ergebnisse")
        .setHeader(accTrain, accTest)
        .h2("Trainings Daten")
        .printMatrix(Arrays.copyOfRange(mBuffer.getTrainingsData(), 1, 20), nn)
        .h2("Test Daten")
        .printMatrix(Arrays.copyOfRange(mBuffer.getTestData(), 1, 20), nn);

        html.writeHTML();

        html = new MNISTHTML().log(accTrain, accTest);
        
    }

    public static String getAcuracy(FFNetwork.NeuralNetwork nn, MnistMatrix[] mnistMatrix) {
        int iterations = mnistMatrix.length;
        int correct = 0;
        double acuracy = 0.0;
        for (int i = 0; i < iterations; i++) {
            int Hindex = getHighestIndex(nn.Querry(mnistMatrix[i].getInputs()));
            int label = mnistMatrix[i].getLabel();
            if (Hindex == label) {
                correct++;
            }
        }
        acuracy = (double) correct / (double) iterations * 100;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(acuracy) + "%";
    }

    public static int getHighestIndex(double[] numbers) {
        int maxNumberIndex = 0;

        // Schleife durch das Array laufen und die größte Zahl finden
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > numbers[maxNumberIndex]) {
                maxNumberIndex = i;
            }
        }
        return maxNumberIndex;
    }
}
