package crawling;


import java.util.Collections;
import java.io.Serializable;

import edu.uci.ics.crawler4j.url.WebURL;

@SuppressWarnings("unused")
public class URLInfo implements Serializable{
	private static final long serialVersionUID = 4192157425959534708L;
	private WebURL URLName;
	//private boolean isFetched; //fetched
	private boolean isCrawled; //crawled 
	private boolean isWordIndexed; //Indexed by Word indxer
	private boolean isPhraseIndexed; //Indexed by Phrase indexer
	private boolean isDocClustered;
	private boolean isWordClustered;
	private boolean isPhraseClustered;
	private boolean isCoreSite;
	
	public URLInfo()
	{
		
	}
	
	public URLInfo(WebURL uRLName, boolean isCrawled, boolean isWordIndexed,
			boolean isPhraseIndexed, boolean isDocClustered, boolean isWordClustered, boolean isPhraseClustered,boolean isCoreSite) {
		super();
		URLName = uRLName;
		///this.isFetched = isFetched;
		this.isCrawled = isCrawled;
		this.isWordIndexed = isWordIndexed;
		this.isPhraseIndexed = isPhraseIndexed;
		this.isDocClustered = isDocClustered;
		this.isWordClustered = isWordClustered;
		this.isPhraseClustered = isPhraseClustered;
		this.isCoreSite = isCoreSite;
	}
	
	public WebURL getURLName() {
		return URLName;
	}
	//------------------------------------------------------------
	/*public boolean isFetched() {
		return isFetched;
	}*/
	public boolean isCrawled() {
		return isCrawled;
	}
		public boolean isWordIndexed() {
		return isWordIndexed;
	}
	public boolean isDocClustered() {
		return isDocClustered;
	}
	public boolean isWordClustered() {
		return isWordClustered;
	}
	public boolean isPhraseClustered() {
		return isPhraseClustered;
	}
	
	public boolean isPhraseIndexed() {
		return isPhraseIndexed;
	}
	public boolean isCoreSite() {
		return isCoreSite;
	}
	//------------------------------------------------------------------
	public void setURLName(WebURL uRLName) {
		URLName = uRLName;
	}
	/*public void setFetched(boolean isFetched) {
		this.isFetched = isFetched;
	}*/
	public void setDocClustered(boolean isDocClustered) {
		this.isDocClustered = isDocClustered;
	}
	public void setWordClustered(boolean isWordClustered) {
		this.isWordClustered = isWordClustered;
	}
	public void setPhraseClustered(boolean isPhraseClustered) {
		this.isPhraseClustered = isPhraseClustered;
	}
	public void setCrawled(boolean isCrawled) {
		this.isCrawled = isCrawled;
	}
	public void setWordIndexed(boolean isWordIndexed) {
		this.isWordIndexed = isWordIndexed;
	}
	public void setPhraseIndexed(boolean isPhraseIndexed) {
		this.isPhraseIndexed = isPhraseIndexed;
	}
	public void setCoreSite(boolean isCoreSite) {
		this.isCoreSite = isCoreSite;
	}
	//------------------------------------------------------------------
	@Override
	public boolean equals(Object object){
		if (object == null) return false;
	    if (object == this) return true;
	    if (this.getClass() != object.getClass())return false;
	    URLInfo u1 = (URLInfo)object;
	    if(this.getURLName().equals(u1.getURLName()))return true;
	    return false;
	} 
	
}

