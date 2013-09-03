package com.wise.util;

public class ContacterData {
	private String contacter;
	private String tree_path;
	public String getContacter() {
		return contacter;
	}
	public void setContacter(String contacter) {
		this.contacter = contacter;
	}
	public String getTree_path() {
		return tree_path;
	}
	public void setTree_path(String tree_path) {
		this.tree_path = tree_path;
	}
	@Override
	public String toString() {
		return "ContacterData [contacter=" + contacter + ", tree_path="
				+ tree_path + "]";
	}	
}