package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class MappedNodeList implements NodeList, Iterable<Node> {
	
	private NodeList source;
	
	private Map<String, List<Node>> nodeMap = new HashMap<String, List<Node>>();
	
	public MappedNodeList(NodeList source) {
		this.source = source;
		
		for(Node node : this) {
			String name = node.getNodeName();
			List<Node> nodes = nodeMap.get(name);
			if(nodes == null) {
				nodes = new ArrayList<Node>();
				nodeMap.put(name, nodes);
			}
			nodes.add(node);
		}
	}
	
	public List<Node> getAll(String nodeName) {
		return nodeMap.getOrDefault(nodeName, new ArrayList<Node>());
	}

	@Override
	public Node item(int index) {
		return source.item(index);
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

	@Override
	public Iterator<Node> iterator() {
		return new Iterator<Node>() {
			
			private int i = -1;
			
			@Override
			public Node next() {
				i++;
				return item(i);
			}
			
			@Override
			public boolean hasNext() {
				return getLength() > i + 1;
			}
		};
	}

}
