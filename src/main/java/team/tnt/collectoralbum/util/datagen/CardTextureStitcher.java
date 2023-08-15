package team.tnt.collectoralbum.util.datagen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public final class CardTextureStitcher {

    public static final File OVERLAY_DIR = new File("./cards/overlay");
    public static final File BACKGROUND_DIR = new File("./cards/background");
    public static final File OUTPUT_DIR = new File("./cards/out");

    public static final int IMAGE_X = 8;
    public static final int IMAGE_Y = 4;

    public static void main(String[] args) {
        OUTPUT_DIR.mkdirs();
        Component[] backgrounds = loadOverlays(BACKGROUND_DIR);
        Component[] overlays = loadOverlays(OVERLAY_DIR);
        for (Component background : backgrounds) {
            for (Component overlay : overlays) {
                stitch(background, overlay);
            }
        }
    }

    private static void stitch(Component background, Component overlay) {
        BufferedImage result = copy(background.image());
        BufferedImage overlayImage = overlay.image();
        for (int y = 0; y < overlayImage.getHeight(); y++) {
            for (int x = 0; x < overlayImage.getWidth(); x++) {
                int rgba = overlayImage.getRGB(x, y);
                if (rgba >>> 24 > 0) {
                    result.setRGB(IMAGE_X + x, IMAGE_Y + y, rgba);
                }
            }
        }
        try {
            String filename = background.identifier() + "_" + overlay.identifier() + "_card.png";
            File resultFile = new File(OUTPUT_DIR, filename);
            ImageIO.write(result, "PNG", resultFile);
        } catch (IOException e) {
            throw new RuntimeException("Image saving failed", e);
        }
    }

    private static BufferedImage copy(BufferedImage image) {
        ColorModel model = image.getColorModel();
        boolean alphaPreMultiplied = image.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(model, raster, alphaPreMultiplied, null);
    }

    private static Component[] loadOverlays(File dir) {
        File[] files = dir.listFiles();
        Component[] components = new Component[files.length];
        int index = 0;
        for (File file : files) {
            BufferedImage image;
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                throw new RuntimeException("Image reading failed", e);
            }
            components[index++] = new Component(getIdentifier(file.getName()), image);
        }
        return components;
    }

    private static String getIdentifier(String filename) {
        return filename.replaceAll("\\.[^.]+$", "");
    }

    private record Component(String identifier, BufferedImage image) {
    }
}
