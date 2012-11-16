package org.opennms.features.topology.plugins.topo.adapter.internal;

import org.opennms.features.topology.api.topo.Vertex;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

public class SimpleVertex implements Vertex {
	
	private final String m_namespace;
	private final String m_id;
	private String m_label;
	private String m_tooltpText;
	private String m_iconKey;
	private String m_styleName;

	public SimpleVertex(String namespace, String id) {
		m_namespace = namespace;
		m_id = id;
	}

	@Override
	public String getNamespace() {
		return m_namespace;
	}

	@Override
	public String getId() {
		return m_id;
	}

	@Override
	public String getKey() {
		return getNamespace()+":"+getId();
	}
	
	@Override
	public Object getItemId() {
		return getNamespace()+":"+getId();
	}

	@Override
	public Item getItem() {
		return new BeanItem<SimpleVertex>(this);
	}

	public String getTooltpText() {
		return m_tooltpText;
	}

	public void setTooltpText(String tooltpText) {
		m_tooltpText = tooltpText;
	}

	@Override
	public String getLabel() {
		return m_label;
	}

	public void setLabel(String label) {
		m_label = label;
	}

	@Override
	public String getTooltipText() {
		return m_tooltpText;
	}

	@Override
	public String getIconKey() {
		return m_iconKey;
	}

	public void setIconKey(String iconKey) {
		m_iconKey = iconKey;
	}

	@Override
	public String getStyleName() {
		return m_styleName;
	}

	public void setStyleName(String styleName) {
		m_styleName = styleName;
	}

}
