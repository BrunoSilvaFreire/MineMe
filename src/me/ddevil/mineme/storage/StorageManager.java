/*
 * Copyright (C) 2016 Selma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ddevil.mineme.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Selma
 */
public class StorageManager {

    public static void addReset(Mine m) throws IOException, ParseException {
        File minestoragefile = new File(MineMe.storageFolder, m.getName() + ".mineme");
        File tempminestoragefile = File.createTempFile("temp" + m.getName() + ".mineme", ".mineme", minestoragefile.getParentFile());
        //getjson
        JSONObject json = getMineJson(m);
        //Update broken blocks and reset
        long brokenblocks = json.get("totalbrokenblocks") == null ? 0 : (long) json.get("totalbrokenblocks");
        json.put("totalbrokenblocks", brokenblocks += m.getMinedBlocks());
        long resets = json.get("totaltimesreseted") == null ? 0 : (long) json.get("totaltimesreseted");
        json.put("totaltimesreseted", resets++);
        //Reencode
        String updatedString = Base64.getEncoder().encodeToString(json.toJSONString().getBytes());
        //Write the new file
        try (FileWriter fileWriter = new FileWriter(tempminestoragefile)) {
            fileWriter.write(updatedString);
            fileWriter.close();
        }
        //Change old file to new
        minestoragefile.delete();
        tempminestoragefile.renameTo(minestoragefile);

    }

    public static JSONObject getMineJson(Mine m) throws IOException, ParseException {
        //Get file
        File minestoragefile = new File(MineMe.storageFolder, m.getName() + ".mineme");
        if (!minestoragefile.exists()) {
            minestoragefile = createNewMineFile(m);
        }
        String filetext = "";

        BufferedReader reader;
        try ( //Readers
                FileReader fileReader = new FileReader(minestoragefile)) {
            reader = new BufferedReader(fileReader);
            //Read the file
            String line = reader.readLine();
            while (line != null) {
                filetext += line;
                line = reader.readLine();
            }   //Decode the file
            filetext = new String(Base64.getDecoder().decode(filetext));
            //Get as json
            JSONObject json = (JSONObject) new JSONParser().parse(filetext);
            reader.close();
            return json;
        }
    }

    private static File createNewMineFile(Mine m) throws IOException {
        JSONObject json = new JSONObject();
        json.put("mine", m.getName());
        json.put("totalbrokenblocks", 0);
        json.put("totaltimesreseted", 0);
        File storagefile = new File(MineMe.storageFolder, m.getName() + ".mineme");
        try (FileWriter fileWriter = new FileWriter(storagefile)) {
            fileWriter.write(Base64.getEncoder().encodeToString(json.toJSONString().getBytes()));
        }
        return storagefile;
    }

    public static long getTotalBrokenBlocks(Mine m) {
        try {
            JSONObject json = getMineJson(m);
            return json.get("totalbrokenblocks") == null ? 0 : (long) json.get("totalbrokenblocks");
        } catch (Exception e) {
            MineMe.instance.printException("There was an error getting total broken blocks from mine " + m.getName() + "!", e);
            return 0l;
        }
    }

    public static long getTotalResets(Mine m) {
        try {
            JSONObject json = getMineJson(m);
            return json.get("totaltimesreseted") == null ? 0 : (long) json.get("totaltimesreseted");
        } catch (Exception e) {
            MineMe.instance.printException("There was an error getting total resets from mine " + m.getName() + "!", e);
            return 0l;
        }
    }
}
