package Avro_FlinkLogic_KafkaProds.TruckSignal.TruckSignalGeneration;

import com.schemas.TruckSignal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
public class TruckSignalGenerator {

    public TruckSignalGenerator() {
    }

    public TruckSignal getRandomTruckSignal(int signalInt) {
        Random random = new Random();

        // String fields
        int index = random.nextInt(1); // Choose number of customers/owners
        String[] customers = {"mans", "cw", "olof"};
        String customer = customers[index]; // Customer identifier
        String number = String.format("%02d", random.nextInt(50)); // Choose number of vehicles
        String vehicle = "TRUCK-ID-" + number; // Vehicle identifier
        String signal = "default";
        float value = 0.00f;
        String padding = ""; // Add around 1000 characters here to pad each tuple to 1KB.
        boolean makeNegative;

        // Switch case for signal selection
        switch(signalInt) {
            case 0:
                signal = "engineOilTemp";
                value = randomAndRound(random, 10,90);
                break;
            case 1:
                signal = "engineOilLevel";
                value = randomAndRound(random, 25,75);
                break;
            case 2:
                signal = "engineSpeed";
                value = randomAndRound(random, 1000,500);
                break;
            case 3:
                signal = "totalVehicleDistance";
                value = randomAndRound(random, 1000,1000);
                break;
            case 4:
                signal = "wheelBasedVehicleDistance";
                value = randomAndRound(random, 100,0);
                break;
            case 5:
                signal = "engineTotalFuelUsed";
                value = randomAndRound(random, 100,6500);
                break;
            case 6:
                signal = "roadInclination";
                value = randomAndRound(random, 10,0);
                makeNegative = random.nextBoolean();
                if (makeNegative) {
                    value *= -1;
                }
                break;
            case 7:
                signal = "frontAxleLoad";
                value = randomAndRound(random, 300,5000);
                break;
            case 8:
                signal = "acceleratorPedalPosition";
                value = randomAndRound(random, 80,0);
                break;
            case 9:
                signal = "totalWeight";
                value = randomAndRound(random, 1000,25000);
                break;
        }

        LocalDateTime eventTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));

        return new TruckSignal(
                customer,
                vehicle,
                signal,
                value,
                padding,
                eventTime
        );
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    /**
     *
     * Sets the NaN-value rate
     *
     */
    public static float floatNaNWrapper(float value) {
        float NaN_prob = 1.0f; // Set NaN-rate. Default is 100%.
        Random rand = new Random();
        if(rand.nextFloat() < NaN_prob){
            value = Float.NaN;
        }
        return value;
    }

    public static float randomAndRound(Random rand, int range, int offset){
        return floatNaNWrapper(round(rand.nextFloat() * range, 2) + offset);
    }

}




