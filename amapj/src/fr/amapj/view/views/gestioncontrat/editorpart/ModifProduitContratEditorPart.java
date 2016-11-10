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
 package fr.amapj.view.views.gestioncontrat.editorpart;

import com.vaadin.data.util.BeanItem;

import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.LigneContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet de modifier l'entete du contrat, c'est à dire son nom
 * et la date limite d'inscription 
 */
@SuppressWarnings("serial")
public class ModifProduitContratEditorPart extends FormPopup
{
	private ModeleContratDTO modeleContrat;
	
	/**
	 * 
	 */
	public ModifProduitContratEditorPart(Long id)
	{
		popupTitle = "Modification des produits d'un contrat";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(id);
		
		item = new BeanItem<ModeleContratDTO>(modeleContrat);
		
		
	}
	
	protected void addFields()
	{
		// Le producteur
		Searcher prod = new Searcher(SearcherList.PRODUCTEUR);
		prod.bind(binder, "producteur");
		form.addComponent(prod);
		prod.setEnabled(false);
		
		// Les produits
		CollectionEditor<LigneContratDTO> f1 = new CollectionEditor<LigneContratDTO>("Produits", (BeanItem) item, "produits", LigneContratDTO.class);
		f1.addSearcherColumn("produitId", "Nom du produit",FieldType.SEARCHER, null,SearcherList.PRODUIT,prod);
		f1.addColumn("prix", "Prix du produit", FieldType.CURRENCY, null);
		binder.bind(f1, "produits");
		form.addComponent(f1);
	}
	
	


	protected void performSauvegarder()
	{
		// Sauvegarde du contrat
		new GestionContratService().updateProduitModeleContrat(modeleContrat);
	}
	
	/**
	 * Vérifie si il n'y a pas déjà des contrats signés, qui vont empecher de modifier les produits
	 */
	@Override
	protected String checkInitialCondition()
	{
		int nbInscrits = new GestionContratService().getNbInscrits(modeleContrat.id);
		if (nbInscrits!=0)
		{
			String str = "Vous ne pouvez plus modifier les produits ou les prix de ce contrat<br/>"+
						 "car "+nbInscrits+" adhérents ont déjà souscrits à ce contrat<br/>."+
						 "Une seule solution est possible :<br/><ul>"+
						 "<li>Supprimez les contrats signés par les adhérents, si ce sont des données de test</li>"+
						 "</ul>";
			return str;
		}
		
		return null;
	}
	
}
