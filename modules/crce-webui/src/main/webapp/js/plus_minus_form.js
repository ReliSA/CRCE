var element_id = 2;

function plus() {
	var x = document.getElementById('options');  
	
	var name = document.createElement('input');  
	name.setAttribute('name', 'name_' + element_id  ); 
	name.setAttribute('type', 'text');
	
	var value = document.createElement('input');  
	value.setAttribute('name', 'value_' + element_id ); 
	value.setAttribute('class', 'text');
	value.setAttribute('type', 'text');
	
	var nameText = document.createTextNode('Name: ');
	var valueText = document.createTextNode('Value: ');
	  
	var tr = document.createElement('tr');
	tr.setAttribute('id', 'option_' + element_id);
	var thName = document.createElement('th');
	var thValue = document.createElement('th');
	var tdName = document.createElement('td');
	var tdValue = document.createElement('td');
	
	thName.appendChild(nameText);
	tdName.appendChild(name);
	thValue.appendChild(valueText);
	tdValue.appendChild(value);
	
	tr.appendChild(thName);
	tr.appendChild(tdName);
	tr.appendChild(thValue);
	tr.appendChild(tdValue);
	
	x.appendChild(tr); 
	
	element_id = element_id + 1;
}

function minus(){  
    if( element_id <= 2 )  
        return;  
        
    element_id = element_id - 1;
    var d = document.getElementById('options');
    var olddiv = document.getElementById( 'option_' + element_id );  
    
    d.removeChild(olddiv);    
} 