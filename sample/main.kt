import java.io.File;
import java.awt.*;

fun main( args: Array<String> ) {
    if ( args.size == 0 ) { println("引数を指定してください"); return; }
    val f = File( args[0] );
    if ( !ImageUtil.isImageFile(f) ) { println( "ファイルが存在しない、もしくは画像ファイルではありません" ); return; }

    val imgUtil = ImageUtil(f);
    imgUtil.addBorderText( "Hello World !", Font(Font.SERIF, Font.PLAIN, 30), Color.white, Color.black, 30, 30, 2 );
    imgUtil.save( /* 出力ファイルパス */, ImageExt.PNG );
}
