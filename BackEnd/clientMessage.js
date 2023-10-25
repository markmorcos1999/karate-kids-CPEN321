module.exports = class ClientMessage{

    constructor(message) {
        
		
		head = findFirst(message, ":");
		
		this.subject = message.substr(0, head);
		
		data = message.slice(head)
		
		this.data = listDivide(data, ",", "-")
    }
	
	//The least general function in javascript be like:
	function findFirst(query, toFind){
		for(int i = 0; i < query.length; i++){
			if(query[i] == toFind){
				return i;
			}
		}
		return null;
	}
	
	function listDivide(data, divider, sep){
		
		
	}
    
}