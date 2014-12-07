package framework;

public class Camera{
	
	//Camera:
	private double camAngleX_term;
	private double camAngleY_term;
	private double camH;
	private double camX;
	private double camY;
	
	//Viewport:
	public volatile double viewX;
	public volatile double viewY;
	public volatile double viewW;
	public volatile double viewH;
			
	public Camera(double camX, double camY, double camH, double camAngleX, double camAngleY){
		this.camH = camH;
		setCameraAngle(camAngleX, camAngleY);
		moveCameraTo(camX, camY);
	}
	
	public void moveCameraTo(double camX, double camY){
		synchronized(this){
			viewX = (camX - camAngleX_term);
			viewY = (camY - camAngleY_term);
			this.camX = camX;
			this.camY = camY;
		}
	}
	
	public void moveCameraWith(double dCamX, double dCamY){
		synchronized(this){
			this.camX += dCamX;
			this.camY += dCamY;
			viewX = (camX - camAngleX_term);
			viewY = (camY - camAngleY_term);
		}
	}
	
	public void setCameraAngle(double camAngleX, double camAngleY){
		synchronized(this){
			this.camAngleX_term = camH*Math.tan(camAngleX/2);
			this.camAngleY_term = camH*Math.tan(camAngleY/2);
			viewW = 2*camAngleX_term;
			viewH = 2*camAngleY_term;		
		}
	}
	
	public void setCameraHeightTo(double camH){
		synchronized(this){
			this.camAngleX_term = this.camAngleX_term/this.camH*camH;
			this.camAngleY_term = this.camAngleY_term/this.camH*camH;
			this.camH = camH;
			viewX = (camX - camAngleX_term);
			viewY = (camY - camAngleY_term);
			viewW = 2*camAngleX_term;
			viewH = 2*camAngleY_term;
		}
	}
	
	public void setViewWidthTo(double viewW){
		this.setCameraHeightTo(camH*viewW/this.viewW);
	}
	
	public void setViewHeightTo(double viewH){
		this.setCameraHeightTo(camH*viewH/this.viewH);
	}
	
	public void changeCameraHeightWith(double dCamH){
		synchronized(this){
			if(this.camH+dCamH<=0){
				return;
			}
			this.camAngleX_term = this.camAngleX_term/this.camH*(this.camH+dCamH);
			this.camAngleY_term = this.camAngleY_term/this.camH*(this.camH+dCamH);
			this.camH += dCamH;
			viewX = (camX - camAngleX_term);
			viewY = (camY - camAngleY_term);
			viewW = 2*camAngleX_term;
			viewH = 2*camAngleY_term;
		}
	}
	
}
