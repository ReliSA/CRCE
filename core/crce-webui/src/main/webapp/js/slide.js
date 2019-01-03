$(document).ready(function(){
	        $(".informace").hide();
	      
	        $(".popis").click(function(){
	          $(this).parent().next(".informace").slideToggle(500)
	          return false;
	        });
	        
	        $(".rozbalit").click(function(){
	          $(".informace").slideDown(500)
	          return false;
	        });
	        
	        $(".sbalit").click(function(){
	          $(".informace").slideUp(500)
	          return false;
	        });
	      
	      });