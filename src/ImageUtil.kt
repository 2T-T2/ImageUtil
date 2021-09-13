import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Polygon;
import java.awt.Font;
import java.awt.Point;
import java.awt.AlphaComposite;
import javax.imageio.*;
import kotlin.math.*;
import java.awt.geom.Ellipse2D;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

enum class ImageExt {
    PNG, JPG, BMP, WBMP, GIF
}

class ImageUtil( img :BufferedImage ){
    private var img: BufferedImage;
    private val originalImage: BufferedImage;

    init {
        this.img = img;
        this.originalImage = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.dispose();
        }
    }
    constructor( f:File ) : this( ImageIO.read(f) ) {}
    constructor( path:String ) : this( ImageIO.read( File(path) ) ) {}

    fun resize(width: Int, height: Int) {
        img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, width, height, null);
            g.dispose();
        }
    }
    fun resize(wScale: Double, hScale: Double) { resize( img.width*wScale , img.height*hScale); }

    fun crop(x: Int, y: Int, width: Int, height: Int) {
        img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage( img, 0, 0, width, height, x, y, x+width, y+height, null );
            g.dispose();
        }
    }
    fun crop(width: Int, height: Int) { crop(img.width/2-width/2, img.height/2-height/2, width, height) }

    fun transparentByColor(color: Color) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( Color(img.getRGB(x, y)).equals(color) ) {
                        it.setRGB(x, y, 0);
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun transparentByColor(r: Int, g: Int, b: Int) { transparentByColor(Color(r, g, b)); }

    fun transparentByRect(rect: Rectangle) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        it.setRGB(x, y, 0);
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun transparentByRect(x: Int, y: Int, w: Int, h: Int) { transparentByRect(Rectangle(x, y, w, h)); }

    fun transparentByEllipse2D(ellipse: Ellipse2D.Double) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( ellipse.contains(x.toDouble(), y.toDouble()) ) {
                        it.setRGB(x, y, 0);
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun transparentByEllipse2D(ellipse: Ellipse2D.Float) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( ellipse.contains(x.toDouble(), y.toDouble()) ) {
                        it.setRGB(x, y, 0);
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }

    fun addLine(color: Color, x1: Int, y1: Int, x2: Int, y2: Int, thickness: Float) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.stroke = BasicStroke(thickness);
            g.drawLine(x1, y1, x2, y2);
            g.dispose();
        }
    }
    fun addLine(color: Color, x1: Int, y1: Int, x2: Int, y2: Int) { addLine(color, x1, y1, x2, y2, 1.0f) }
    fun addLine(x1: Int, y1: Int, x2: Int, y2: Int, thickness: Float) { addLine(Color.black, x1, y1, x2, y2, thickness) }
    fun addLine(x1: Int, y1: Int, x2: Int, y2: Int) { addLine(Color.black, x1, y1, x2, y2, 1.0f) }

    fun addArcLine(color: Color, x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int, thickness: Float) {
         img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.stroke = BasicStroke(thickness);
            g.drawArc(x, y, width, height, startAngle, arcAngle)
            g.dispose();
        }
    }
    fun addArcLine(color: Color, x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) { addArcLine(color, x, y, width, height, startAngle, arcAngle, 1.0f); }
    fun addArcLine(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) { addArcLine(Color.black, x, y, width, height, startAngle, arcAngle, 1.0f); }
    fun addArcLine(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int, thickness: Float) { addArcLine(Color.black, x, y, width, height, startAngle, arcAngle, thickness); }

    fun addPolyLine(color: Color, xPoints: IntArray, yPoints: IntArray, nPoints: Int, thickness: Float) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.stroke = BasicStroke(thickness);
            g.drawPolyline(xPoints, yPoints, nPoints)
            g.dispose();
        }
    }

    fun addLineOval(color: Color, x: Int, y: Int, width: Int, height: Int, thickness: Float) {
         img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.stroke = BasicStroke(thickness);
            g.drawOval(x, y, width, height)
            g.dispose();
        }
    }
    fun addLineOval(color: Color, x: Int, y: Int, width: Int, height: Int) { addLineOval(color, x, y, width, height, 1.0f); }
    fun addLineOval(x: Int, y: Int, width: Int, height: Int) { addLineOval(Color.black, x, y, width, height, 1.0f); }
    fun addLineOval(x: Int, y: Int, width: Int, height: Int, thickness: Float) { addLineOval(Color.black, x, y, width, height, thickness); }

    fun addLineCircle(color: Color, x: Int, y:Int, r: Int, thickness: Float) { addLineOval(color, x, y, r*2, r*2, thickness); }
    fun addLineCircle(color: Color, x: Int, y:Int, r: Int) { addLineCircle(color, x, y, r, 1.0f); }
    fun addLineCircle(x: Int, y:Int, r: Int) { addLineCircle(Color.black, x, y, r, 1.0f); }
    fun addLineCircle(x: Int, y:Int, r: Int, thickness: Float) { addLineCircle(Color.black, x, y, r, thickness); }

    fun addLinePolygon(color: Color, xPoints: IntArray, yPoints: IntArray, nPoints: Int, thickness: Float) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.stroke = BasicStroke(thickness);
            g.drawPolygon(xPoints, yPoints, nPoints)
            g.dispose();
        }
    }
    fun addLinePolygon(color: Color, xPoints: IntArray, yPoints: IntArray, nPoints: Int) { addLinePolygon(color, xPoints, yPoints, nPoints, 1.0f) }
    fun addLinePolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int, thickness: Float ) { addLinePolygon(Color.black, xPoints, yPoints, nPoints, thickness) }
    fun addLinePolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) { addLinePolygon(xPoints, yPoints, nPoints, 1.0f) }
    fun addLinePolygon(color: Color, polygon: Polygon, thickness: Float) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.stroke = BasicStroke(thickness);
            g.drawPolygon(polygon);
            g.dispose();
        }
    }
    fun addLinePolygon(color: Color, polygon: Polygon) { addLinePolygon(color, polygon, 1.0f); }
    fun addLinePolygon(polygon: Polygon, thickness: Float) { addLinePolygon(Color.black, polygon, thickness); }
    fun addLinePolygon(polygon: Polygon) { addLinePolygon(Color.black, polygon, 1.0f); }

    fun addLineRect(color: Color, x: Int, y: Int, width: Int, height: Int, thickness: Float) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.stroke = BasicStroke(thickness);
            g.drawRect(x, y, width, height)
            g.dispose();
        }
    }
    fun addLineRect(x: Int, y: Int, width: Int, height: Int, thickness: Float) { addLineRect(Color.black, x, y, width, height, thickness) }
    fun addLineRect(color: Color, x: Int, y: Int, width: Int, height: Int) { addLineRect(color, x, y, width, height, 1.0f) }
    fun addLineRect(x: Int, y: Int, width: Int, height: Int) { addLineRect(Color.black, x, y, width, height, 1.0f) }
    fun addLineRect(color: Color, rect: Rectangle, thickness: Float) { addLineRect(color, rect.x, rect.y, rect.width, rect.height, thickness) }
    fun addLineRect(rect: Rectangle, thickness: Float) { addLineRect(Color.black, rect, thickness) }
    fun addLineRect(color: Color, rect: Rectangle) { addLineRect(color, rect, 1.0f) }
    fun addLineRect(rect: Rectangle) { addLineRect(Color.black, rect, 1.0f) }

    fun addLineRoundedRect(color: Color, x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int, thickness: Float) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.stroke = BasicStroke(thickness);
            g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
            g.dispose();
        }
    }
    fun addLineRoundedRect(x: Int, y: Int, width: Int, height: Int, thickness: Float) { addLineRoundedRect(Color.black, x, y, width, height, 3, 3, thickness) }
    fun addLineRoundedRect(color: Color, x: Int, y: Int, width: Int, height: Int) { addLineRoundedRect(color, x, y, width, height, 3, 3, 1.0f) }
    fun addLineRoundedRect(x: Int, y: Int, width: Int, height: Int) { addLineRoundedRect(Color.black, x, y, width, height, 3, 3, 1.0f) }
    fun addLineRoundedRect(color: Color, rect: Rectangle, thickness: Float) { addLineRoundedRect(color, rect.x, rect.y, rect.width, rect.height, 3, 3, thickness) }
    fun addLineRoundedRect(rect: Rectangle, thickness: Float) { addLineRoundedRect(Color.black, rect, thickness) }
    fun addLineRoundedRect(color: Color, rect: Rectangle) { addLineRoundedRect(color, rect, 1.0f) }
    fun addLineRoundedRect(rect: Rectangle) { addLineRoundedRect(Color.black, rect, 1.0f) }
    fun addLineRoundedRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int, thickness: Float) { addLineRoundedRect(Color.black, x, y, width, height, arcWidth, arcHeight, thickness) }
    fun addLineRoundedRect(color: Color, x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) { addLineRoundedRect(color, x, y, width, height, arcWidth, arcHeight, 1.0f) }
    fun addLineRoundedRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) { addLineRoundedRect(Color.black, x, y, width, height, arcWidth, arcHeight, 1.0f) }
    fun addLineRoundedRect(color: Color, rect: Rectangle, arcWidth: Int, arcHeight: Int, thickness: Float) { addLineRoundedRect(color, rect.x, rect.y, rect.width, rect.height, arcWidth, arcHeight, thickness) }
    fun addLineRoundedRect(rect: Rectangle, arcWidth: Int, arcHeight: Int, thickness: Float) { addLineRoundedRect(Color.black, rect, arcWidth, arcHeight, thickness) }
    fun addLineRoundedRect(color: Color, rect: Rectangle, arcWidth: Int, arcHeight: Int) { addLineRoundedRect(color, rect, arcWidth, arcHeight, 1.0f) }
    fun addLineRoundedRect(rect: Rectangle, arcWidth: Int, arcHeight: Int) { addLineRoundedRect(Color.black, rect, arcWidth, arcHeight, 1.0f) }

    fun addFilledArc(color: Color, x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
           val g = it.createGraphics();
           g.drawImage(img, 0, 0, img.width, img.height, null);
           g.color = color;
           g.fillArc(x, y, width, height, startAngle, arcAngle)
           g.dispose();
       }
   }
    fun addFilledArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) { addFilledArc(Color.black, x, y, width, height, startAngle, arcAngle); }

    fun addFilledOval(color: Color, x: Int, y: Int, width: Int, height: Int) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
           val g = it.createGraphics();
           g.drawImage(img, 0, 0, img.width, img.height, null);
           g.color = color;
           g.fillOval(x, y, width, height)
           g.dispose();
       }
   }
   fun addFilledOval(x: Int, y: Int, width: Int, height: Int) { addFilledOval(Color.black, x, y, width, height); }

   fun addFilledCircle(color: Color, x: Int, y:Int, r: Int) { addFilledOval(color, x, y, r*2, r*2); }
   fun addFilledCircle(x: Int, y:Int, r: Int) { addFilledCircle(Color.black, x, y, r); }

    fun addFilledPolygon(color: Color, xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.fillPolygon(xPoints, yPoints, nPoints)
            g.dispose();
        }
    }
    fun addFilledPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) { addFilledPolygon(Color.black, xPoints, yPoints, nPoints) }
    fun addFilledPolygon(color: Color, polygon: Polygon) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.fillPolygon(polygon);
            g.dispose();
        }
    }
    fun addFilledPolygon(polygon: Polygon) { addFilledPolygon(Color.black, polygon); }

    fun addFilledRect(color: Color, x: Int, y: Int, width: Int, height: Int) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.fillRect(x, y, width, height)
            g.dispose();
        }
    }
    fun addFilledRect(x: Int, y: Int, width: Int, height: Int) { addFilledRect(Color.black, x, y, width, height) }
    fun addFilledRect(color: Color, rect: Rectangle) { addFilledRect(color, rect.x, rect.y, rect.width, rect.height) }
    fun addFilledRect(rect: Rectangle) { addFilledRect(Color.black, rect) }

    fun addFilledRoundedRect(color: Color, x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.color = color;
            g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
            g.dispose();
        }
    }
    fun addFilledRoundedRect(x: Int, y: Int, width: Int, height: Int) { addFilledRoundedRect(Color.black, x, y, width, height, 3, 3) }
    fun addFilledRoundedRect(color: Color, x: Int, y: Int, width: Int, height: Int) { addFilledRoundedRect(color, x, y, width, height, 3, 3) }
    fun addFilledRoundedRect(color: Color, rect: Rectangle) { addFilledRoundedRect(color, rect.x, rect.y, rect.width, rect.height, 3, 3) }
    fun addFilledRoundedRect(rect: Rectangle) { addFilledRoundedRect(Color.black, rect) }
    fun addFilledRoundedRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) { addFilledRoundedRect(Color.black, x, y, width, height, arcWidth, arcHeight) }
    fun addFilledRoundedRect(color: Color, rect: Rectangle, arcWidth: Int, arcHeight: Int) { addFilledRoundedRect(color, rect.x, rect.y, rect.width, rect.height, arcWidth, arcHeight) }
    fun addFilledRoundedRect(rect: Rectangle, arcWidth: Int, arcHeight: Int) { addFilledRoundedRect(Color.black, rect, arcWidth, arcHeight) }

    fun addText(str: String, font: Font, color: Color, x: Int, y:Int) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.font = font;
            g.color = color;
            g.drawString(str, x, y);
            g.dispose();
        }
    }
    fun addText(str: String, x: Int, y: Int) { addText(str, Font(Font.SERIF, Font.PLAIN, 12),Color.BLACK, x, y); }
    fun addText(str: String, fontSize: Int, x: Int, y: Int) { addText(str, Font(Font.SERIF, Font.PLAIN, fontSize), Color.black, x, y); }
    fun addText(str: String, fontSize: Int, color: Color, x: Int, y: Int) { addText(str, Font(Font.SERIF, Font.PLAIN, fontSize), color, x, y); }

    fun addBorderText(str: String, font: Font, innerColor: Color, outerColor: Color, x :Int, y :Int, thickness: Int) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.font = font;
            g.color = outerColor;
            for(dx in -thickness..thickness) {
                for(dy in -thickness..thickness) {
                    g.drawString(str, x+dx, y+dy)
                }
            }
            g.color = innerColor;
            g.drawString(str, x, y);
            g.dispose();
        }
    }
    fun addBorderText(str: String, innerColor: Color, outerColor: Color, x: Int, y: Int) { addBorderText(str, Font(Font.SERIF, Font.PLAIN, 24), innerColor, outerColor, x, y, 1); }
    fun addBorderText(str: String, fontSize: Int, innerColor: Color, outerColor: Color, x: Int, y: Int) { addBorderText(str, Font(Font.SERIF, Font.PLAIN, fontSize), innerColor, outerColor, x, y, 1); }
    fun addBorderText(str: String, fontSize: Int, innerColor: Color, outerColor: Color, x: Int, y: Int, thickness: Int) { addBorderText(str, Font(Font.SERIF, Font.PLAIN, fontSize), innerColor, outerColor, x, y, thickness); }

    fun addImage( addtionalImg: BufferedImage, x: Int, y: Int, width: Int, height: Int ) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.drawImage(addtionalImg, x, y, width, height, null);
            g.dispose();
        }
    }

    fun replaceColor(beforeCol: Color, afterCol: Color) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( Color(img.getRGB(x, y)).equals( beforeCol ) ) {
                        it.setRGB(x, y, changeRgbaFormat(afterCol.red, afterCol.green, afterCol.blue, afterCol.alpha))
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }

    fun adjustmentRGB(redScale: Float, greenScale: Float, blueScale: Float) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    val rgba = rgbInt2RgbIntArr(img.getRGB(x, y))
                    val r = max( min( (rgba[0] * redScale  ).toInt(), 255), 0);
                    val g = max( min( (rgba[1] * greenScale).toInt(), 255), 0);
                    val b = max( min( (rgba[2] * blueScale ).toInt(), 255), 0);
                    val a = rgba[3];
                    it.setRGB(x, y, changeRgbaFormat(r, g, b, a) );
                }
            }
        }
    }

    fun gammaCorrection( gamma: Float ) {
        val calc = { i: Int ->
            255.0 * ( (i.toDouble()/255).pow(1.toDouble()/gamma) )
        }
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    val rgba = rgbInt2RgbIntArr( img.getRGB(x,y) );
                    val r = calc( rgba[0] ).toInt();
                    val g = calc( rgba[1] ).toInt();
                    val b = calc( rgba[2]  ).toInt();
                    val a = rgba[3];
                    it.setRGB(x, y, changeRgbaFormat(r, g, b, a) );
                }
            }
        }
    }

    fun changeHue(rect: Rectangle, hueRad: Float){ 
        val hsv: FloatArray = floatArrayOf(0.0f,0.0f,0.0f);
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        val col = Color( img.getRGB(x, y) );
                        Color.RGBtoHSB(col.red, col.green, col.blue, hsv);
                        hsv[0] = hsv[0] + hueRad;
                        it.setRGB( x, y, Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) );
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun changeHue(rect: Rectangle, hueDeg: Int) { changeHue(rect, deg2RadF(hueDeg)) }
    fun changeHue(x: Int, y: Int, width: Int, height: Int, hueRad: Float ) { changeHue(Rectangle(x, y, width, height), hueRad); }
    fun changeHue(x: Int, y: Int, width: Int, height: Int, hueDeg: Int ) { changeHue(x, y, width, height, deg2RadF(hueDeg)); }
    fun changeHue(hueRad: Float) { changeHue(0,0,img.width,img.height, hueRad) }
    fun changeHue(hueDeg: Int) { changeHue( deg2RadF(hueDeg) )}

    fun setHue(rect: Rectangle, hueRad: Float) {
        val hsv: FloatArray = floatArrayOf(0.0f,0.0f,0.0f);
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        val col = Color( img.getRGB(x, y) );
                        Color.RGBtoHSB(col.red, col.green, col.blue, hsv);
                        hsv[0] = hueRad;
                        it.setRGB( x, y, Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) );
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun setHue(rect: Rectangle, hueDeg: Int) { setHue( rect, deg2RadF(hueDeg) ); }
    fun setHue(x: Int, y: Int, width: Int, height: Int, hueRad: Float ) { setHue(Rectangle(x, y, width, height), hueRad); }
    fun setHue(x: Int, y: Int, width: Int, height: Int, hueDeg: Int ) { setHue(x, y, width, height, deg2RadF(hueDeg)); }
    fun setHue(hueRad: Float) { setHue(0,0,img.width,img.height, hueRad) }
    fun setHue(hueDeg: Int) { setHue( deg2RadF(hueDeg) )}

    fun changeSaturation(rect: Rectangle, saturationScale: Float) {
        val hsv: FloatArray = floatArrayOf(0.0f,0.0f,0.0f);
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        val col = Color( img.getRGB(x, y) );
                        Color.RGBtoHSB(col.red, col.green, col.blue, hsv);
                        hsv[1] = min( saturationScale * hsv[1], 1.0f );
                        it.setRGB( x, y, Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) );
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun changeSaturation(saturationScale: Float) { changeSaturation(Rectangle(0,0, img.width,img.height), saturationScale) }
    fun changeSaturation(x: Int, y: Int, width: Int, height: Int, saturationScale: Float) { changeSaturation(Rectangle(x, y, width, height), saturationScale) }

    fun addSaturation(rect: Rectangle, saturation: Float) {
        val hsv: FloatArray = floatArrayOf(0.0f,0.0f,0.0f);
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        val col = Color( img.getRGB(x, y) );
                        Color.RGBtoHSB(col.red, col.green, col.blue, hsv);
                        hsv[1] = min( saturation + hsv[1], 1.0f );
                        it.setRGB( x, y, Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) );
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun addSaturation(x: Int, y: Int, width: Int, height: Int, saturation: Float) { addSaturation(Rectangle(x, y, width, height), saturation) }
    fun addSaturation(saturation: Float) { addSaturation(Rectangle(0,0, img.width,img.height), saturation) }

    fun setSaturation(rect: Rectangle, saturation: Float) {
        val hsv: FloatArray = floatArrayOf(0.0f,0.0f,0.0f);
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        val col = Color( img.getRGB(x, y) );
                        Color.RGBtoHSB(col.red, col.green, col.blue, hsv);
                        hsv[1] = saturation;
                        it.setRGB( x, y, Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) );
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun setSaturation(x: Int, y: Int, width: Int, height: Int, saturation: Float) { setSaturation(Rectangle(x, y, width, height), saturation); }
    fun setSaturation(saturation: Float) { setSaturation( Rectangle(0,0, img.width, img.height), saturation ); }

    fun changeBrightness(rect: Rectangle, brightnessScale: Float) {
        val hsv: FloatArray = floatArrayOf(0.0f,0.0f,0.0f);
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        val col = Color( img.getRGB(x, y) );
                        Color.RGBtoHSB(col.red, col.green, col.blue, hsv);
                        hsv[2] = min(hsv[2] * brightnessScale, 1.0f);
                        it.setRGB( x, y, Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) );
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
               }
            }
        }
    }
    fun changeBrightness(x: Int, y: Int, width: Int, height: Int, brightnessScale: Float) { changeBrightness( Rectangle(x, y, width, height), brightnessScale ); }
    fun changeBrightness(brightnessScale: Float) { changeBrightness( Rectangle(0,0, img.width, img.height), brightnessScale); }

    fun setBrightness(rect: Rectangle, brightness: Float) {
        val hsv: FloatArray = floatArrayOf(0.0f,0.0f,0.0f);
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        val col = Color( img.getRGB(x, y) );
                        Color.RGBtoHSB(col.red, col.green, col.blue, hsv);
                        hsv[2] = min(brightness, 1.0f);
                        it.setRGB( x, y, Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) );
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun setBrightness(x: Int, y: Int, width: Int, height: Int, brightness: Float) { setBrightness(Rectangle(x,y,width,height), brightness) }
    fun setBrightness(brightness: Float) { setBrightness( Rectangle(0,0, img.width,img.height), brightness ) }

    fun addBrightness(rect: Rectangle, brightness: Float) {
        val hsv: FloatArray = floatArrayOf(0.0f,0.0f,0.0f);
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    if( rect.contains(x, y) ) {
                        val col = Color( img.getRGB(x, y) );
                        Color.RGBtoHSB(col.red, col.green, col.blue, hsv);
                        hsv[2] = min(brightness+hsv[2], 1.0f);
                        it.setRGB( x, y, Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) );
                    }else {
                        it.setRGB(x, y, img.getRGB(x, y))
                    }
                }
            }
        }
    }
    fun addBrightness(x: Int, y: Int, width: Int, height: Int, brightness: Float) { addBrightness(Rectangle(x,y,width,height), brightness) }
    fun addBrightness(brightness: Float) { addBrightness( Rectangle(0,0, img.width,img.height), brightness ) }

    fun colorInvert() {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    val rgbArr = rgbInt2RgbIntArr(img.getRGB( x, y ));
                    val invertRgb = changeRgbaFormat(255-rgbArr[0], 255-rgbArr[1], 255-rgbArr[2], rgbArr[3]);
                    it.setRGB(x, y, invertRgb);
                }
            }
        }
    }

    fun toSepia() {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    val rgba = rgbInt2RgbIntArr( img.getRGB(x, y) );
                    val or = rgba[0];
                    val og = rgba[1];
                    val ob = rgba[2];
                    val ng = min( ( (or*0.349f)+(og*0.686f)+(ob*0.168f) ).toInt(), 255 );
                    val nr = min( ( (or*0.393f)+(og*0.769f)+(ob*0.189f) ).toInt(), 255 );
                    val nb = min( ( (or*0.272f)+(og*0.534f)+(ob*0.131f) ).toInt(), 255 );
                    val a = rgba[3];
                    it.setRGB(x, y, changeRgbaFormat(nr, ng, nb, a) );
                }
            }
        }
    }

    fun toMosaic( mosSize: Int ) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1 step mosSize) {
                for(x in 0..img.width-1 step mosSize) {
                    val endX = min( img.width-1 , x+mosSize );
                    val endY = min( img.height-1, y+mosSize );
                    val rgb = calcRgbAve(x, y, endX, endY);
                    for(dy in y..endY) { for(dx in x..endX) { it.setRGB(dx, dy, rgb) } }
                }
            }
        }
    }

    fun toGray(rScale: Float, gScale: Float, bScale: Float) {
        adjustmentRGB(rScale, gScale, bScale);
        setSaturation(0.0f);
    }
    fun toGray() { toGray( 0.2126f, 0.7152f, 0.0722f ); }

    fun blur( blurPower: Int ) {
        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    val endX = min( img.width-1 , x+blurPower );
                    val endY = min( img.height-1, y+blurPower );
                    val rgb = calcRgbAve(x, y, endX, endY);
                    it.setRGB(x,y,rgb)
                }
            }
        }
    }

    fun edge( power: Int, amount: Float ) {
        colorEdge( power, amount )
        setSaturation(0.0f);
    }

    fun colorEdge( power: Int, amount: Float ) {
        val oldImg = getChangedImage();
        blur( power );
        val bluredImg = getChangedImage();

        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB ).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    val oldRgb = rgbInt2RgbIntArr( oldImg.getRGB( x, y ) );
                    val bluredRgb = rgbInt2RgbIntArr( bluredImg.getRGB( x, y ) );
                    val r = max( abs( ((oldRgb[0] - bluredRgb[0])).toInt() ), 0 );
                    val g = max( abs( ((oldRgb[1] - bluredRgb[1])).toInt() ), 0 );
                    val b = max( abs( ((oldRgb[2] - bluredRgb[2])).toInt() ), 0 );
                    val rgba = if ( r == 0 && g == 0 && b == 0 ) 0 else changeRgbaFormat(r, g, b, (255*amount).toInt() )
                    it.setRGB( x, y, rgba )
                }
            }
        }
    }

    fun unsharpmask( power: Int, amount: Float, threshold: Int ) {
        val oldImg = getChangedImage();
        blur( power );
        val bluredImg = getChangedImage();

        img = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB ).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    val oldRgb = rgbInt2RgbIntArr( oldImg.getRGB( x, y ) );
                    val bluredRgb = rgbInt2RgbIntArr( bluredImg.getRGB( x, y ) );

                    val r = min( max( (oldRgb[0] + (oldRgb[0] - bluredRgb[0])*amount ).toInt(), 0), 255);
                    val g = min( max( (oldRgb[1] + (oldRgb[1] - bluredRgb[1])*amount ).toInt(), 0), 255);
                    val b = min( max( (oldRgb[2] + (oldRgb[2] - bluredRgb[2])*amount ).toInt(), 0), 255);

                    val rgba = if ( ((r + g + b) / 3).toInt() < threshold ) changeRgbaFormat(oldRgb[0], oldRgb[1], oldRgb[2], 255) else changeRgbaFormat(r, g, b, 255 )
                    it.setRGB( x, y, rgba )
                }
            }
        }
    }

    /**
     * @param rad 回転度数(Radian度数)
     * @param arrowSizeChange 回転後にサイズが変わるのを許すか
     */
    fun rote( rad: Double, arrowSizeChange: Boolean ) {
        val rotatePoint: (Point, Double) -> Point = {point: Point, angle: Double ->
            val th = atan2(point.y.toDouble(), point.x.toDouble());
            val norm = sqrt( (point.x * point.x + point.y * point.y).toDouble() );
            val x = norm * cos(th - angle);
            val y = norm * sin(th - angle);
            Point( x.toInt(), y.toInt() );
        }
        val beforeImg = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.dispose();
        }
        val angle = rad;
        val af = AffineTransform();

        val p1 = rotatePoint( Point(img.width, img.height), angle );
        val p2 = rotatePoint( Point(-img.width, img.height), angle );
        val w = max( abs(p1.x), abs(p2.x) )
        val h = max( abs(p1.y), abs(p2.y) )
        val dx = w  / 2.0 - ( (img.width * Math.cos(angle) - img.height * Math.sin(angle)) / 2.0 );
        val dy = h / 2.0 - ( (img.width * Math.sin(angle) + img.height * Math.cos(angle)) / 2.0 );
        af.setToTranslation(dx, dy);        
        af.rotate( angle );

        img = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        val afo = AffineTransformOp( af, AffineTransformOp.TYPE_BICUBIC );
        afo.filter(beforeImg, img);

        if ( !arrowSizeChange ) { resize( beforeImg.width, beforeImg.height ); }
    }
    fun rote( deg: Int, arrowSizeChange: Boolean ) { rote( deg2Rad( deg ), arrowSizeChange ) }
    fun rote( rad: Double ) { rote(rad, false) }
    fun rote( deg: Int ) { rote( deg2Rad( deg ), false ); }

    fun save( fileName: String, fileExt: ImageExt, hasAlpha: Boolean ) {
        val type = if( arrayOf(ImageExt.PNG, ImageExt.GIF).contains( fileExt ) && hasAlpha ) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB;
        val outImg = BufferedImage(img.width, img.height, type).also {
            val g = it.createGraphics();
            g.drawImage(img, 0, 0, img.width, img.height, null);
            g.dispose();
        }
        ImageIO.write(outImg, fileExt.toString(), File(fileName));
    }
    fun save( fileName: String, fileExt: ImageExt ) { save(fileName, fileExt, true); }

    fun reset() { img = getOriginalImage(); }

    fun getOriginalImage(): BufferedImage {
        return BufferedImage(originalImage.width, originalImage.height, BufferedImage.TYPE_INT_ARGB).also {
            val g = it.createGraphics();
            g.drawImage(originalImage, 0, 0, originalImage.width, originalImage.height, null);
            g.dispose();
        }
    }
    fun getChangedImage(): BufferedImage {
        return BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB).also {
            for(y in 0..img.height-1) {
                for(x in 0..img.width-1) {
                    it.setRGB( x, y, img.getRGB(x, y) );
                }
            }
        }
    }
    fun getWidth(): Int = img.width;
    fun getHeight(): Int = img.height;
    fun getFontMetrics(font: Font): FontMetrics {
        val g = img.createGraphics();
        val fontMetrics = g.getFontMetrics(font);
        g.dispose();
        return fontMetrics;
    }

    // static methods
    companion object {
        fun isImageFile( f: File ): Boolean { return f.exists() && arrayOf( ".png", ".jpg", ".bmp", ".wbmp", ".gif" ).contains( f.name.substring(f.name.lastIndexOf(".")).lowercase() ); }
    }

    private fun deg2Rad( deg: Int ): Double = (deg * PI /180);
    private fun deg2RadF( deg: Int ): Float = (deg * PI /180).toFloat();
    private fun calcRgbAve( startX: Int, startY: Int, endX: Int, endY: Int ): Int {
        val calcRedAve    = { rgbs: List<Int> -> (rgbs.map{ Color(it).red   }).average().toInt() }
        val calcGreenAve  = { rgbs: List<Int> -> (rgbs.map{ Color(it).green }).average().toInt() }
        val calcBlueAve   = { rgbs: List<Int> -> (rgbs.map{ Color(it).blue  }).average().toInt() }
        val calcAlphaAve  = { rgbs: List<Int> -> (rgbs.map{ Color(it).alpha }).average().toInt() }
        val rgbs: MutableList<Int> = mutableListOf();
        for(dy in startY..endY) { for(dx in startX..endX) { rgbs.add( img.getRGB(dx, dy) ) } }
        val r = calcRedAve( rgbs );
        val g = calcGreenAve( rgbs );
        val b = calcBlueAve( rgbs );
        val a = calcAlphaAve( rgbs );
        return changeRgbaFormat(r, b, g, a);
    }
    private fun rgbInt2RgbIntArr( rgb: Int ): IntArray {
        val col = Color(rgb);
        return intArrayOf( col.red, col.green, col.blue, col.alpha );
    }
    private fun changeRgbaFormat( r: Int, g: Int, b: Int, a: Int ): Int {
        if( r > 255 || g > 255 || b > 255 || a > 255 ||
            r <  0  || g <  0  || b <  0  || a <  0  ) { throw IllegalArgumentException("RGBA の値は 0 < 255 の範囲で指定") }
        return (a shl 24) or (r shl 16) or (g shl 8) or (b);
    }
}
