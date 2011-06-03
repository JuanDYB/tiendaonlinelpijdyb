function loadEditor (){
    tinyMCE.init({
        //modo
        mode : "textareas",
        //tema
        theme : "advanced",
        //barras de herramientas
        theme_advanced_toolbar_location: "top",
        theme_advanced_toolbar_align : "left",
        theme_advanced_statusbar_location : "bottom",
        
        //Plugings
        plugins : "preview,searchreplace,insertdatetime,print,fullscreen",
        plugin_preview_width : "500",
        plugin_preview_height : "600",

        //Botones
        theme_advanced_buttons1 : "bold,italic,underline,strikethrough,separator,link,unlink,separator,undo,redo,separator,cleanup,code,\n\
separator,cut,copy,paste,separator,charmap,separator,preview",
        
        theme_advanced_buttons2 : "bullist,numlist,separator,search,replace,separator,insertdate,inserttime,separator,print,fullscreen",
        theme_advanced_buttons3 : "",
        
        //Guardar on submit
        add_form_submit_trigger : true,
        
        //Lenguaje
        language : "es",
        
        //Skin
        skin: "o2k7",
        skin_variant : "black",
        
        //Recogedor de eventos para guardar y que se pueda validar con el validador javascript
        setup: function (ed){
            ed.onEvent.add(function(ed, e) {
                ed.save();
            });

        }
    });
    
}


