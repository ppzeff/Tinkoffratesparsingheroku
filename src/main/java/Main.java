import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.net.URL;

public class Main {
    private static final String mdb = "mongodb+srv://root:TnVYTfgJ3kFW5ia@cluster0.rh5zv.mongodb.net/test";
    private static final String mdb2 = "mongodb+srv://root:TnVYTfgJ3kFW5ia@cluster0.rh5zv.mongodb.net/test>?retryWrites=true&w=majority";

    public static void main(String[] args) throws Exception {
        URL url = new URL("https://api.tinkoff.ru/v1/currency_rates/");
        URL url2 = new URL("https://www.tinkoff.ru/api/v1/currency_rates");
        MongoClient client = new MongoClient(new MongoClientURI(mdb2));
        MongoDatabase mongoDatabase = client.getDatabase("data");
        MongoCollection<Document> collection = mongoDatabase.getCollection("test");
        Payload payload;
        Rates ratesUSD; //840
        Rates ratesEUR; //978
        Rates ratesGBP; //826

        double lastRUBfromUSD = 0;
        double lastRUBtoUSD = 0;
        double lastRUBfromEUR = 0;
        double lastRUBtoEUR = 0;
        double lastRUBfromGBP = 0;
        double lastRUBtoGBP = 0;
        boolean change = false;
        for (int i = 0; i < 1_000_000; i++) {
            payload = Utils.getPayloadFromUrl(url2);
            ratesUSD = Utils.getRatesUSDRUB(payload, 840);
            ratesEUR = Utils.getRatesUSDRUB(payload, 978);
            ratesGBP = Utils.getRatesUSDRUB(payload, 826);

            Document document = new Document(ratesUSD.getCategory(), payload.getLastUpdate().getMilliseconds());

            if ((ratesEUR.getBuy() != 0 & ratesEUR.getBuy() != lastRUBfromEUR)
                    || (ratesEUR.getSell() != 0 & ratesEUR.getSell() != lastRUBtoEUR)) {
                document.put("buyRUBfromEUR", ratesEUR.getBuy());
                document.put("sellRUBtoEUR", ratesEUR.getSell());
                lastRUBfromEUR = ratesEUR.getBuy();
                lastRUBtoEUR = ratesEUR.getSell();
                change = true;
            }

            if ((ratesUSD.getBuy() != 0 && ratesUSD.getBuy() != lastRUBfromUSD)
                    || (ratesUSD.getSell() != 0 && ratesUSD.getSell() != lastRUBtoUSD)) {
                document.put("buyRUBfromUSD", ratesUSD.getBuy());
                document.put("sellRUBtoUSD", ratesUSD.getSell());
                lastRUBfromUSD = ratesUSD.getBuy();
                lastRUBtoUSD = ratesUSD.getSell();
                change = true;
            }

            if ((ratesGBP.getBuy() != 0 && ratesGBP.getBuy() != lastRUBfromGBP) ||
                    (ratesGBP.getSell() != 0 && ratesGBP.getSell() != lastRUBtoGBP)) {
                document.put("buyRUBfromGBP", ratesGBP.getBuy());
                document.put("sellRUBtoGBP", ratesGBP.getSell());
                lastRUBfromGBP = ratesGBP.getBuy();
                lastRUBtoGBP = ratesGBP.getSell();
                change = true;
            }

            if (change) {
                collection.insertOne(document);
                change = false;
            }
/*
            if (ratesEUR.getBuy() == 0 && ratesGBP.getBuy() == 0 && ratesUSD.getBuy() == 0) {

            }


            if (
                    (lastRUBtoUSD == ratesUSD.getBuy()) &&
                            (lastRUBtoEUR == ratesEUR.getBuy()) &&
                            (lastRUBtoGBP == ratesGBP.getBuy()) &&
                            (lastRUBfromUSD == ratesUSD.getSell()) &&
                            (lastRUBfromEUR == ratesEUR.getSell()) &&
                            (lastRUBfromGBP == ratesGBP.getSell())
            ) {

            } else {
                document.put("buyRUBfromUSD", ratesUSD.getBuy());
                document.put("sellRUBtoUSD", ratesUSD.getSell());

                document.put("buyRUBfromEUR", ratesEUR.getBuy());
                document.put("sellRUBtoEUR", ratesEUR.getSell());

                document.put("buyRUBfromGBP", ratesGBP.getBuy());
                document.put("sellRUBtoGBP", ratesGBP.getSell());

                collection.insertOne(document);

                lastRUBtoUSD = ratesUSD.getBuy();
                lastRUBtoEUR = ratesEUR.getBuy();
                lastRUBtoGBP = ratesGBP.getBuy();
                lastRUBfromUSD = ratesUSD.getSell();
                lastRUBfromEUR = ratesEUR.getSell();
                lastRUBfromGBP = ratesGBP.getSell();
*/
            Thread.sleep(10_000);
//            System.out.println(i);
        }
    }
}

