//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.stickbit;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception var29) {
            System.out.println("No dark theme :(");
        }

        List<String> prodkeys = new ArrayList();
        new ArrayList();

        try {
            Files.createDirectory(Paths.get(System.getProperty("user.home") + "/.switch"));
        } catch (Exception var28) {
        }

        try {
            prodkeys = Files.readAllLines(Paths.get("prod.keys"));
            Files.move(Paths.get("prod.keys"), Paths.get(System.getProperty("user.home") + "/.switch/prod.keys"), StandardCopyOption.REPLACE_EXISTING);
            Files.move(Paths.get("title.keys"), Paths.get(System.getProperty("user.home") + "/.switch/title.keys"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception var27) {
            try {
                prodkeys = Files.readAllLines(Paths.get(System.getProperty("user.home") + "/.switch/prod.keys"));
            } catch (Exception var26) {
                errorMessage("Failed to read your title.keys and prod.keys.\nPut them in this folder once you get them.\n(" + System.getProperty("user.dir") + ")\nYou can get them using Lockpick RCM, then Lockpick.", "Keys failed!");
                System.exit(-1);
            }
        }

        String sdseed = "none! :(";
        Iterator var4 = ((List) prodkeys).iterator();

        String drive;
        while (var4.hasNext()) {
            drive = (String) var4.next();
            if (drive.startsWith("sd_seed")) {
                sdseed = drive.substring(drive.indexOf(" = ") + 3);
            }
        }

        if (sdseed.equals("none! :(")) {
            errorMessage("Failed to find your SD seed. Make sure your prod.keys file is correct!", "SD Seed Failed");

            try {
                sdseed = JOptionPane.showInputDialog("If you believe your prod.keys is correct, enter your SD seed here.");
                ((List) prodkeys).add("sd_seed = " + sdseed);
                Files.write(Paths.get(System.getProperty("user.home") + "/.switch/prod.keys"), (Iterable) prodkeys);
            } catch (Exception var31) {
                if (var31.toString().equals("java.lang.NullPointerException")) {
                    System.exit(0);
                }

                System.exit(-1);
            }

            main(args);
            System.exit(-1);
        }

        List<String> drives = new ArrayList();
        File[] var33 = File.listRoots();
        int var6 = var33.length;

        for (int var7 = 0; var7 < var6; ++var7) {
            File f = var33[var7];
            drives.add(f.toString().replace("\\", ""));
        }

        drive = customButtons("Select your SD card. Make sure it's plugged in! If it isn't, just close this window.", "SD Card", drives.toArray()).toString();
        if (drive == null) {
            System.exit(0);
        }

        try {
            Process proc = Runtime.getRuntime().exec("hactoolnet.exe --sdseed " + sdseed + " --listtitles " + drive + " -t switchfs");
            BufferedReader text = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            List<String> out = new ArrayList();
            ArrayList idNoBase = new ArrayList();

            String line;
            while ((line = text.readLine()) != null) {
                out.add(line);
            }

            for (int i = 0; i < out.size(); ++i) {
                if (!((String) out.get(i)).contains(" Application ") && !((String) out.get(i)).contains(" Patch ")) {
                    out.remove(i);
                    --i;
                }
            }

            String[] parsedData = new String[out.size()];

            String outPath;
            for (int i = 0; i < out.size(); ++i) {
                StringBuilder backwardB = new StringBuilder();

                for (int j = ((String) out.get(i)).length(); j > 0; --j) {
                    backwardB.append(((String) out.get(i)).substring(j - 1, j));
                }

                String backward = backwardB.toString();
                parsedData[i] = backward.substring(0, backward.indexOf("  "));
                backwardB = new StringBuilder();

                for (int j = parsedData[i].length(); j > 0; --j) {
                    backwardB.append(parsedData[i].substring(j - 1, j));
                }

                parsedData[i] = backwardB.toString();
                if (((String) out.get(i)).substring(0, ((String) out.get(i)).indexOf(parsedData[i])).contains("Application")) {
                    outPath = "Application";
                } else {
                    outPath = "Patch";
                }

                if (i == 0 && outPath.equals("Patch")) {
                    idNoBase.add(((String) out.get(i)).substring(0, 16));
                }

                if (i > 0 && outPath.equals("Patch") && !((String) out.get(i - 1)).contains(parsedData[i])) {
                    idNoBase.add(((String) out.get(i)).substring(0, 16));
                }

                if (((String) out.get(i)).replace(parsedData[i], "").contains(" GB ")) {
                    parsedData[i] = parsedData[i] + " - " + ((String) out.get(i)).substring(((String) out.get(i)).indexOf(" GB ") + 4, ((String) out.get(i)).indexOf(parsedData[i])).trim();
                } else if (((String) out.get(i)).replace(parsedData[i], "").contains(" MB ")) {
                    parsedData[i] = parsedData[i] + " - " + ((String) out.get(i)).substring(((String) out.get(i)).indexOf(" MB ") + 4, ((String) out.get(i)).indexOf(parsedData[i])).trim();
                } else if (((String) out.get(i)).replace(parsedData[i], "").contains(" KB ")) {
                    parsedData[i] = parsedData[i] + " - " + ((String) out.get(i)).substring(((String) out.get(i)).indexOf(" KB ") + 4, ((String) out.get(i)).indexOf(parsedData[i])).trim();
                }

                parsedData[i] = ((String) out.get(i)).substring(0, 16) + " - " + parsedData[i];
                parsedData[i] = parsedData[i] + " - " + outPath;
            }

            String choice = customButtons("Please choose the game you want to dump.", "Choose File", parsedData).toString();
            outPath = JOptionPane.showInputDialog("Please enter the file path for your dumped ROM.");

            try {
                Files.createDirectory(Paths.get(outPath));
            } catch (FileAlreadyExistsException var25) {
            }

            String gameBase = "none :(";

            try {
                if (idNoBase.contains(choice.substring(0, 16))) {
                    gameBase = JOptionPane.showInputDialog("It looks like you're dumping an update that doesn't have a base. Please provide its XCI.");
                }
            } catch (NullPointerException var24) {
                gameBase = "none :(";
            }

            if (gameBase.equals("none :(")) {
                proc = Runtime.getRuntime().exec("hactoolnet.exe -t switchfs --sdseed " + sdseed + " --title " + choice.substring(0, 16) + " --exefsdir " + outPath + "/ExeFS --romfsdir " + outPath + "/ROMFS " + drive);
                text = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                while (true) {
                    if (text.readLine() != null) {
                        continue;
                    }
                }
            } else {
                proc = Runtime.getRuntime().exec("hactoolnet.exe -t xci --outdir " + System.getProperty("user.home").replace("\\", "/") + "/TEMPNCA.nca/BaseGame " + gameBase);
                text = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                while (true) {
                    if (text.readLine() == null) {
                        File[] items = (new File(System.getProperty("user.home") + "/TEMPNCA.nca/BaseGame/secure/")).listFiles();
                        long biggest = 0L;
                        String nameBigBase = "";
                        File[] var19 = items;
                        int var20 = items.length;

                        int var21;
                        for (var21 = 0; var21 < var20; ++var21) {
                            File f = var19[var21];
                            if (f.length() > biggest) {
                                biggest = f.length();
                                nameBigBase = f.getName();
                            }
                        }

                        proc = Runtime.getRuntime().exec("hactoolnet.exe -t switchfs --basenca " + System.getProperty("user.home").replace("\\", "/") + "/TEMPNCA.nca/BaseGame/secure/" + nameBigBase + " --sdseed " + sdseed + " --title " + choice.substring(0, 16) + " --outdir " + System.getProperty("user.home").replace("\\", "/") + "/TEMPNCA.nca/Update/ " + drive);
                        text = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                        while (text.readLine() != null) {
                        }

                        items = (new File(System.getProperty("user.home") + "/TEMPNCA.nca/Update/")).listFiles()[0].listFiles();
                        biggest = 0L;
                        String nameBigUpdate = "";
                        File[] var44 = items;
                        var21 = items.length;

                        for (int var45 = 0; var45 < var21; ++var45) {
                            File f = var44[var45];
                            if (f.length() > biggest) {
                                biggest = f.length();
                                nameBigUpdate = f.getName();
                            }
                        }

                        proc = Runtime.getRuntime().exec("hactoolnet.exe -t nca --basenca " + System.getProperty("user.home").replace("\\", "/") + "/TEMPNCA.nca/BaseGame/secure/" + nameBigBase + " --plaintext " + outPath + " --romfsdir " + outPath + "/ROMFS --exefsdir " + outPath + "/ExeFS " + System.getProperty("user.home").replace("\\", "/") + "/TEMPNCA.nca/Update/" + (new File(System.getProperty("user.home") + "/TEMPNCA.nca/Update/")).listFiles()[0].getName() + "/" + nameBigUpdate);
                        text = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                        while (text.readLine() != null) {
                        }

                        Runtime.getRuntime().exec("cmd.exe rd /q /s " + System.getProperty("user.home").replace("\\", "/") + "/TEMPNCA.nca/");
                        break;
                    }
                }
            }
        } catch (Exception var30) {
            if (var30.toString().equals("java.lang.NullPointerException")) {
                System.exit(0);
            }

            var30.printStackTrace();
            errorMessage("Failed to run hactoolnet. Make sure you have it in this folder and that your files and paths are valid.", "File error!");
            System.exit(-1);
        }

        infoMessage("All done! If everything worked correctly, the ExeFS and ROMFS were saved to your specified folder. Happy modding :)", "Finished");
        System.exit(0);
    }

    static void errorMessage(String message, String title) {
        JOptionPane.showConfirmDialog((Component) null, message, title, -1, 0);
    }

    static void infoMessage(String message, String title) {
        JOptionPane.showConfirmDialog((Component) null, message, title, -1, 1);
    }

    static Object customButtons(String message, String title, Object[] items) {
        return JOptionPane.showInputDialog((Component) null, message, title, 1, (Icon) null, items, "C:");
    }
}
