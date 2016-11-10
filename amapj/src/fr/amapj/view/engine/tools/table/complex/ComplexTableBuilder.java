/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
 * 
 *  This file is part of AmapJ.
 *  
 *  AmapJ is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  AmapJ is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with AmapJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
 package fr.amapj.view.engine.tools.table.complex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.service.engine.generator.CoreGenerator;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.tools.table.TableColumnInfo;
import fr.amapj.view.engine.tools.table.TableColumnType;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Outil pour créer les tables specifiques avec saisie dans la table 
 *
 */
public class ComplexTableBuilder<T>
{
	
	static public interface ToValue<T>
	{
		public Object toValue(T t);
	}
	
	static public interface CallBack<T>
	{
		public void onClick(T t);
	}
	
	
	static public interface ToGenerator<T>
	{
		public CoreGenerator getGenerator(T t);
	}
	
	
	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private List<TableColumnInfo<T>> cols;
	
	private Table t;
	
	private List<T> beans;
	
	public ComplexTableBuilder(List<T> beans)
	{
		this.beans = beans;
		
		cols = new ArrayList<TableColumnInfo<T>>();
		
	}
	
	//
	public void addString(String title, boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, null, false,width, TableColumnType.STRING, toVal,null, null);
	}
	
	public void addString(String title, String property,boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, property,false,width, TableColumnType.STRING, toVal,null, null);
	}
	
	//
	public void addCurrency(String title, boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, null, false,width, TableColumnType.CURRENCY, toVal,null, null);
	}
	
	public void addCurrency(String title, String property,boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, property,false,width, TableColumnType.CURRENCY, toVal,null, null);
	}
	
	//
	public void addDate(String title, boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, null, false,width, TableColumnType.DATE, toVal,null, null);
	}
	
	public void addDate(String title, String property,boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, property,false,width, TableColumnType.DATE, toVal,null, null);
	}
	
	
	//
	public void addCheckBox(String title, boolean editable,int width,ToValue<T> toVal,CallBack<T> onClic)
	{
		addColumn(title, null,false,width, TableColumnType.CHECK_BOX, toVal,onClic, null);
	}
		
	public void addCheckBox(String title, String property,boolean editable,int width,ToValue<T> toVal,CallBack<T> onClic)
	{
		addColumn(title, property,false,width, TableColumnType.CHECK_BOX, toVal,onClic, null);
	}
	
	
	//
	public void addButton(String title, int width,ToValue<T> toVal,CallBack<T> onClic)
	{
		addColumn(title, null,false,width, TableColumnType.BUTTON, toVal,onClic, null);
	}
		
	public void addButton(String title, String property,int width,ToValue<T> toVal,CallBack<T> onClic)
	{
		addColumn(title, property,false,width, TableColumnType.BUTTON, toVal,onClic, null);
	}
	
	
	public void addLink(String title, int width,ToValue<T> toVal,ToGenerator<T> generator)
	{
		addColumn(title, null,false,width, TableColumnType.LINK, toVal,null,generator);
	}
	
	
	
	
	private void addColumn(String title, String property,boolean editable, int width,TableColumnType type, ToValue<T> toVal,CallBack<T> onClic, ToGenerator<T> generator)
	{
		cols.add(new TableColumnInfo<T>(title, property,editable,width,type, toVal,onClic,generator));
	}
	
	
	public void buildComponent(Layout contentLayout)
	{
		
		startHeader("tete", 70);
		
		for (TableColumnInfo<T> col : cols)
		{
			addHeaderBox(col.title, col.width+13);
		}
		contentLayout.addComponent(header1);
		
		
		// Construction du contenu de la table
		t = new Table();
		int index = 0;
		for (TableColumnInfo<T> col : cols)
		{
			String property = col.property;
			if (property==null)
			{
				property = "property"+index;
			}
			t.addContainerProperty(property, getClass(col.type), null);
			index++;
		}
		
		// Remplissage des lignes
		index = 0;
		for (T bean : beans)
		{
			Object[] cells = computeCell(bean);
			t.addItem(cells, index);
			index++;
		}
		

		t.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		t.setSelectable(true);
		t.setSortEnabled(false);
		t.setPageLength(15);
		
		contentLayout.addComponent(t);
	}
	
	private Class<?> getClass(TableColumnType type)
	{
		switch (type)
		{
		case STRING:
		case INTEGER:
		case DATE:
		case CURRENCY:
			return Label.class;
			
		case CHECK_BOX:
			return CheckBox.class;
			
		case BUTTON:
			return Button.class;
			
		case LINK:
			return Link.class;

		default:
			throw new AmapjRuntimeException();
		}
	}
	
	
	
	private Object[] computeCell(T bean)
	{
		Object[] cells = new Object[cols.size()];
		
		int index = 0;
		for (TableColumnInfo<T> col : cols)
		{
			cells[index] = createPart(col,bean);
			index++;
		}
		
		return cells;
	}
	

	private Object createPart(TableColumnInfo<T> col, T bean)
	{
		switch (col.type)
		{
		case STRING:
			Object o = col.toVal.toValue(bean);
			if (o==null)
			{
				o="";
			}
			return createLabel( o.toString(),col.width);

		case DATE:
			return createLabel( df.format( (Date) col.toVal.toValue(bean)),col.width);
			
		case INTEGER:
			return createLabel( Integer.toString( (Integer) col.toVal.toValue(bean)),col.width);
			
		case CURRENCY:
			return createLabel( new CurrencyTextFieldConverter().convertToString( (Integer) col.toVal.toValue(bean)),col.width);
			
		case CHECK_BOX:
			return createCheckBox((Boolean) col.toVal.toValue(bean), col.width);
			
		case BUTTON:
			return createButton( col.toVal.toValue(bean).toString(), col.width,col.onClic,bean);
			
		case LINK:
			return createLink( col.toVal.toValue(bean).toString(), col.width,col.generator,bean);
			
		default:
			throw new AmapjRuntimeException();
		}

	}


	private Label createLabel(String msg,int taille)
	{
		Label l = new Label(msg);
		l.addStyleName("align-center");
		l.setWidth(taille+"px");
		return l;
	}
	
	private CheckBox createCheckBox(boolean value,int taille)
	{
		CheckBox cb = new CheckBox();
		cb.addStyleName("align-center");
		cb.setValue(value);
		cb.setWidth(taille+"px");
		cb.setImmediate(true);
		return cb;
	}
	
	private Button createButton(String msg,int taille, CallBack<T> onClic,T t)
	{
		Button cb = new Button(msg);
		cb.addStyleName("align-center");
		cb.setWidth(taille+"px");
		cb.setImmediate(true);
		cb.addClickListener(e->onClic.onClick(t));
		return cb;
	}
	
	private Link createLink(String msg,int taille, ToGenerator<T> generator,T t)
	{
		Link l = LinkCreator.createLink(generator.getGenerator(t));
		
		l.setCaption(msg);
		l.addStyleName("align-center");
		l.setWidth(taille+"px");
		l.setImmediate(true);
		
		return l;
	}
	
	
	
	private TextField createTextField(String value,int taille)
	{
		TextField tf = new TextField();
		tf.setValue(value);
		tf.setWidth(taille+"px");
		tf.setNullRepresentation("");
		tf.setImmediate(true);
		return tf;
	}
	
	
	/**
	 * PARTIE HEADER
	 */
	
	HorizontalLayout header1;
	String styleName;
	int height;
	
	private void startHeader(String styleName,int height)
	{
		header1 = new HorizontalLayout();
		header1.setHeight(null);
		header1.setWidth(null);
		
		this.styleName = styleName;
		this.height = height;
	}
	
	
	private void addHeaderBox(String msg,int taille)
	{
		Label hLabel = new Label(msg);
		hLabel.setWidth((taille+13)+"px");
		hLabel.setHeight(height+"px");
		hLabel.addStyleName(styleName);
		header1.addComponent(hLabel);
	}

	
	
	/**
	 * Retourne le composant à la ligne lineNumber et à la colonne property
	 */
	public AbstractField getComponent(int lineNumber,String property)
	{
		Item item = t.getItem(lineNumber);
		
		AbstractField tf = (AbstractField) item.getItemProperty(property).getValue();
		return tf;
	}
	
	
	
	public void reload(List<T> beans)
	{
		this.beans = beans;
		
		t.removeAllItems();
		
		// Remplissage des lignes
		int index = 0;
		for (T bean : beans)
		{
			Object[] cells = computeCell(bean);
			t.addItem(cells, index);
			index++;
		}
		
	}
	


}
