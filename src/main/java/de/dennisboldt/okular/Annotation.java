package de.dennisboldt.okular;

import de.dennisboldt.api.AnnotationType;

/**
 * Represents an Okular Annotation
 *
 * @author Dennis Boldt
 *
 */
public class Annotation {

	// The l-, r-, t- and b-coordinates
	private Double l = null;
	private Double r = null;
	private Double t = null;
	private Double b = null;

	// The type of the annotation
	private AnnotationType type = null;

	private String text = null;

	// The color like #RRGGBB
	private String color = null;

	/**
	 *
	 * @param l The l-coordinate
	 * @param r The r-coordinate
	 * @param t The t-coordinate
	 * @param b The b-coordinate
	 * @param type The type of the annotation
	 */
	public Annotation(Double l, Double r, Double t, Double b, AnnotationType type) {
		this.l = l;
		this.r = r;
		this.t = t;
		this.b = b;
		this.type = type;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return The left position
	 */
	public Double getL() {
		return l;
	}

	/**
	 * @return The right position
	 */
	public Double getR() {
		return r;
	}

	/**
	 * @return The top position
	 */
	public Double getT() {
		return t;
	}

	/**
	 * @return The bottom position
	 */
	public Double getB() {
		return b;
	}

	/**
	 * The type of this annotation
	 * @return
	 */
	public AnnotationType getType() {
		return type;
	}

	/**
	 * @return The text. connected to this annotation
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param color The color of this annotation in #RRGGBB
	 */
	public void setColor(String color) {
		// TODO: Regex check
		this.color = color;
	}

	/**
	 * @return The color of this annotation in#RRGGBB
	 */
	public String getColor() {
		return color;
	}

	@Override
	public String toString() {
		return this.type  + "=" + this.text;
	}
}
