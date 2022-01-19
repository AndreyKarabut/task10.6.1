/* 1.Прочитать архив
2. считать данные файла
3. применить наценку
4. записать файл
5. заархивировать
*/
import java.io.*;
import java.util.zip.*;

public class Main {
    static String catalog = "src/";
    public static void main(String[] args) throws IOException {
        String inputFileName = catalog + "price.zip";
        File inputUnZipFile = readZip(inputFileName);
        File outputUnZipFile = createNewFile(inputUnZipFile);
        createZip(outputUnZipFile);
    }

    public static File createNewFile(File inputUnZipFile) throws IOException{
        /*Читаем файл, полученый из архива, содержимое модифицируем и записываем в новый текстовый документ
        */
        Reader fis = new FileReader(inputUnZipFile);
        StringBuilder line = new StringBuilder();
        int i;
        while ((i = fis.read()) != -1){
            line.append((char) i);
        }
        fis.close();

        String newPrice = getNewPrice(line.toString());

        File fileNewPrice = new File(String.valueOf(inputUnZipFile).replace(".txt", "_NEW.txt"));
        Writer fos = new FileWriter(fileNewPrice);
        fos.write(newPrice);
        fos.close();
        return fileNewPrice;
    }

    private static String getNewPrice(String line) {
        /*Модифицируем строку по нужным нам параметрам, заранее зная формат данных, разбиваем содержимое
        на массив строк, затем в каждой строке получаем числовое значение и применяем наценку
         */
        StringBuilder newPrice = new StringBuilder();

        String[] lines = line.split(";");
        for (String str:lines) {
            String[] newLines = str.split(" ");
            double cost = Double.parseDouble(newLines[1]);
            cost += cost*0.05;
            newPrice.append(newLines[0]).append(" ").append(cost).append(";\n");
        }


        return newPrice.toString();
    }

    public static File readZip(String fileName) throws IOException {
        /*Разархивируем файл, для этого инициализируем файл с нашим путем,
        затем открываем входящий зип поток, читаем файл из потока, создаем новый текстовый файл,
        куда записываем содержимое из заархивированного файла
         */
        File zip = new File(fileName);
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
        ZipEntry zipEntry = zis.getNextEntry();
        File file = new File(catalog, String.valueOf(zipEntry));
        FileOutputStream fos = new FileOutputStream(file);
        int len;
        byte[] buffer = new byte[1024];
        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        zis.closeEntry();
        zis.close();
        return file;
    }

    public static void createZip(File outputUnZipFile) throws IOException {
        /*Создаем исходящий файловый поток, который передаем в исходящий зип поток,
        инициализируем новый прайс и добавляем его в потк зип через чтение
         */
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(catalog + "price_NEW.zip"));

        File file = new File(String.valueOf(outputUnZipFile));

        out.putNextEntry(new ZipEntry(file.getName()));
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) >= 0)
            out.write(buffer, 0, len);
        fis.close();

        out.close();
    }
}
