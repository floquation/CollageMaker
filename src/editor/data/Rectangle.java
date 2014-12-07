package editor.data;

public class Rectangle {
	public int x,y,w,h;
	
	public Rectangle(int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public String toString(){
		return "Rectangle(" + x + ", " + y + ", " + w + ", " + h + ");";
	}

	public Rectangle clone(){
		return new Rectangle(x,y,w,h);
	}
}
