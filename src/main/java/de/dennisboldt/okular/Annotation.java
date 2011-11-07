package de.dennisboldt.okular;

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
	private Integer type = null;

	private String text = null;

	/**
	 *
	 * @param l The l-coordinate
	 * @param r The r-coordinate
	 * @param t The t-coordinate
	 * @param b The b-coordinate
	 * @param type The type of the annotation
	 */
	public Annotation(Double l, Double r, Double t, Double b, Integer type) {
		this.l = l;
		this.r = r;
		this.t = t;
		this.b = b;
		this.type = type;
	}

	public Annotation(Double l, Double r, Double t, Double b, Integer type, String text) {
		this.l = l;
		this.r = r;
		this.t = t;
		this.b = b;
		this.type = type;
		this.text = text;
	}

	public Double getL() {
		return l;
	}

	public Double getR() {
		return r;
	}

	public Double getT() {
		return t;
	}

	public Double getB() {
		return b;
	}

	public Integer getType() {
		return type;
	}

	public String getText() {
		return text;
	}
}
