package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.analysis.Frame;

public class Node extends Frame {
	
	Set<Node> successors = new HashSet<Node>();

	public Node(int nLocals, int nStack) {
		super(nLocals, nStack);
	}
	
	public Node(Frame src) {
		super(src);
	}
}

