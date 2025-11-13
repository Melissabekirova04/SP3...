package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO {

    /**
     * Gemmer en liste af linjer til en CSV-fil.
     * Første linje bliver altid headeren.
     */
    public void saveData(ArrayList<String> list, String path, String header) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(header + "\n");
            for (String s : list) {
                writer.write(s + "\n");
            }
        } catch (IOException e) {
            System.out.println("Problem saving file '" + path + "': " + e.getMessage());
        }
    }

    /**
     * Læser alle linjer (undtagen første/header-linjen) fra en fil
     * og returnerer dem som en ArrayList.
     */
    public ArrayList<String> readData(String path) {
        ArrayList<String> data = new ArrayList<>();
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("File does not exist: " + path);
            return data;
        }

        try (Scanner scan = new Scanner(file)) {

            // spring headerlinjen over hvis der er mindst én linje
            if (scan.hasNextLine()) {
                scan.nextLine();
            }

            while (scan.hasNextLine()) {
                data.add(scan.nextLine());
            }

        } catch (Exception e) {
            System.out.println("Problem reading file '" + path + "': " + e.getMessage());
        }

        return data;
    }

    /**
     * Læser op til 'length' linjer (undtagen header) fra filen
     * og returnerer dem som et String-array.
     * Hvis der er færre linjer end 'length', vil resten være null.
     */
    public String[] readData(String path, int length) {
        String[] data = new String[length];
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("File does not exist: " + path);
            return data;
        }

        try (Scanner scan = new Scanner(file)) {

            // spring headerlinjen over hvis der er mindst én linje
            if (scan.hasNextLine()) {
                scan.nextLine();
            }

            for (int i = 0; i < data.length && scan.hasNextLine(); i++) {
                data[i] = scan.nextLine();
            }

        } catch (Exception e) {
            System.out.println("Problem reading file '" + path + "': " + e.getMessage());
        }

        return data;
    }
}
