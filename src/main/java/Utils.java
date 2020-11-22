import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Utils {
    private static final int code_RUR = 643;

    public static Rates getRatesUSDRUB(Payload payload, int code) {
        ArrayList<Rates> arrayList = payload.getRates();
        for (Rates rates : arrayList) {
            if (rates.getCategory().equals("DebitCardsOperations") && rates.getFromCurrency().getCode() == code && rates.getToCurrency().getCode() == code_RUR) {
                return rates;
            }
        }
        return null;
    }

    public static Payload getPayloadFromUrl(URL url) throws IOException {
        byte[] jsonData = downloadUrl(url);
        ObjectMapper objectMapper = new ObjectMapper();
        TinkoffRatesJson tinkoffRatesJson = objectMapper.readValue(jsonData, TinkoffRatesJson.class);
        return tinkoffRatesJson.getPayload();
    }

    private static byte[] downloadUrl(URL toDownload) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return outputStream.toByteArray();
    }
}