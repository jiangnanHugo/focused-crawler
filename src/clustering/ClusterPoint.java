package clustering;
import java.io.Serializable;

public class ClusterPoint implements Serializable{
	private static final long serialVersionUID = -2576492414436305093L;
	public ClusterPointType pointType;
	public DocumentBagOfWords document;
	public Integer neighbours;
	public String clusterName;
	public ClusterPoint(ClusterPointType pointType, DocumentBagOfWords document, Integer neighbours, String clusterName) {
		this.pointType = pointType;
		this.document = document;
		this.neighbours = neighbours;
		this.clusterName = clusterName;
	}
}