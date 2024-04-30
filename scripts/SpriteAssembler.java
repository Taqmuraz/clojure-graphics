import java.io.File;
import java.util.Arrays;
import java.util.function.Function;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

public class SpriteAssembler
{
    interface ThrowableFunction<TArg, TResult>
    {
        TResult call(TArg arg) throws Throwable;
    }
    static <TArg, TResult> Function<TArg, TResult> wrapThrowable(ThrowableFunction<TArg, TResult> func)
    {
        return arg ->
        {
            try
            {
                return func.call(arg);
            }
            catch(Throwable th)
            {
                throw new RuntimeException(th);
            }
        };
    }

    public static void main(String[] args)
    {
        String target = args[0];
        String destination = args[1];

        BufferedImage[] sources = Arrays.stream(new File(target).listFiles()).map(wrapThrowable(ImageIO::read)).toArray(BufferedImage[]::new);
        int elementWidth = sources[0].getWidth();
        int elementHeight = sources[0].getHeight();
        int totalWidth = elementWidth * sources.length;
        int totalHeight = elementHeight;

        var resultImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        var resultGraphics = resultImage.getGraphics();
        for(int i = 0; i < sources.length; i++)
        {
            resultGraphics.drawImage(sources[i], elementWidth * i, 0, null);
        }
        try
        {
            ImageIO.write(resultImage, "png", new File(destination));
        }
        catch(Throwable th)
        {
            throw new RuntimeException(th);
        }
    }
}