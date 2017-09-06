package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;

// most fundamental part of game.
// responsible for drawing a trace-line, implementing gravity on itself during renders.
// flies along trajectory when passed initial angle and power, facilitating logic done by main.
public class TrashBall {
	private int x;
	private int y;
	
	private static double gravity = -9.8f;
	
	private double vY = 0;
	private double vX = 0;
	
	private boolean isFlying = false;
	
	
	public TrashBall(int sx, int sy) {
		x = sx;
		y = sy;
	}
	
	public void Shoot(double power, double angle) {
		vX = Math.cos(angle * Math.PI / 180);
		vY = Math.sin(angle * Math.PI / 180);
		isFlying = true;
	}
	
	// every frame, renders the ball.
	public void draw(Batch batch, double dt) {
		if(isFlying) {
			vY += gravity * dt;
			x += vX * dt;
			y += vY * dt;
		}
	}

}
