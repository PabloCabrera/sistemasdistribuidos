package tp2.sobel;

import java.io.Serializable;
import java.io.IOException;
import java.awt.image.BufferedImage; 
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class SerializableImage implements Serializable{

	private BufferedImage image;

	public SerializableImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage(){
		return image;
	}

	public void setImage(BufferedImage image){
		this.image=image;
	}

	private void writeObject(java.io.ObjectOutputStream out)throws IOException{
		ImageIO.write(image,"jpeg",ImageIO.createImageOutputStream(out));
	}

	private void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException{
		image=ImageIO.read(ImageIO.createImageInputStream(in));
	}
}