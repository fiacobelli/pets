''' This script helps you go from beamer (latex) to 
    reveal.js presentations.
'''
import re
import argparse

BEGIN_DOC = re.compile(r'\\begin\{document\}.*')
END_DOC = re.compile(r'\\end{document}.*')
H_END_DOC = r'<!-- END OF CONVERTED SLIDES -->'
######
BEGIN_SLIDE = re.compile(r'\\begin\{frame\}(\[fragile\])?\s*\{(.+?)\}(\{?(.+?)?\})?')
END_SLIDE = re.compile(r'\\end{frame}')
BEGIN_ULIST = re.compile(r'(\\bi|\\bd|\\begin\{itemize\}|\\begin\{description\})\b')
BEGIN_OLIST = re.compile(r'(\\be|\\begin\{enumerate\})\b')
END_ULIST = re.compile(r'(\\ei|\\end\{itemize\}|\\ed|\\end\{description\})\b')
END_OLIST = re.compile(r'(\\ee|\\end\{enumerate\})\b')
ITEM_LIST = re.compile(r'\\item (.+?)$')
TEXT_IT = re.compile(r'\\textit\{(.+)\}')
TEXT_BF = re.compile(r'\\textbf\{(.+)\}')
INCLUDEGRAPHICS = re.compile(r'.*?\\includegraphics(\[.+\])?\{(.+?)\}.*')
BIGSKIP = re.compile(r'\\(big|small|med)skip\b')
HREF = re.compile(r'\\href\{(.+?)\}\{(.+?)\}')
INLINE_MATH = re.compile(r'\$(.+?)\$')
COMMENTS = re.compile(r'\%(.+)$')
LEFT_QUOTE = re.compile(r'``')
RIGHT_QUOTE = re.compile(r"''")


####### REPLACEMENTS FOR HTML ##########
H_BEGIN_SLIDE = r'<section>\n<h2>\2</h2>\n<h4>\4</h4>'
H_END_SLIDE = r'</section>\n'
H_BEGIN_ULIST = r'<ul>'
H_BEGIN_OLIST = r'<ol>'
H_END_ULIST = r'</ul>'
H_END_OLIST = r'</ol>'
H_ITEM_LIST = r'\t<li>\1</li>'
H_TEXT_IT = r'<i>\1</i>'
H_TEXT_BF = r'<strong>\1</strong>'
H_INCLUDEGRAPHICS = r'<img alt="\2" src="\2.png" />'
H_BIGSKIP = r'<br/><br/>'
H_HREF = r'<a href="\1">\2</a>'
H_INLINE_MATH = r'$$\1$$'
H_COMMENTS = r'<!--\1 -->'
H_LEFT_QUOTE = r'&ldquot;'
H_RIGHT_QUOTE = r'&rdquot;'

repl_dict = { "begin slide" :[BEGIN_SLIDE,H_BEGIN_SLIDE],
              "end_slide"   :[END_SLIDE,H_END_SLIDE],
              "begin_ulist" :[BEGIN_ULIST,H_BEGIN_ULIST],
              "end_ulist"   :[END_ULIST,H_END_ULIST],
              "begin_olist" :[BEGIN_OLIST,H_BEGIN_OLIST],
              "end_olist"   :[END_OLIST,H_END_OLIST],
              "item_list"   :[ITEM_LIST,H_ITEM_LIST],
              "textit"      :[TEXT_IT,H_TEXT_IT],
              "textbf"      :[TEXT_BF,H_TEXT_BF],
              "graphics"    :[INCLUDEGRAPHICS,H_INCLUDEGRAPHICS],
              "hrefs"       :[HREF,H_HREF],
              "bigskip"     :[BIGSKIP,H_BIGSKIP],
              #"inline math" :[INLINE_MATH,H_INLINE_MATH],
              "comments"    :[COMMENTS,H_COMMENTS],
              "enddoc"      :[END_DOC,H_END_DOC],
              "left quote"  :[LEFT_QUOTE,H_LEFT_QUOTE],
              "right quote" :[RIGHT_QUOTE,H_RIGHT_QUOTE]
              }


######## ENVIRONMENT REPLACEMENTS #################
TABULAR = re.compile(r'(\\begin\{tabular\}.+?\\end{tabular})',re.DOTALL)
T_ROW = re.compile(r'(.+?)\\\\')
T_DIV = re.compile(r'\s&\s')
T_BEGIN = re.compile(r'\\begin\{tabular\}')
T_END = re.compile(r'\\end\{tabular\}')
#----
MINTED = re.compile(r'(\\begin\{(minted|verbatim)\}.+?\\end\{(minted|verbatim)\})',re.DOTALL)
B_MINTED = re.compile(r'\\begin\{(minted|verbatim)\}')
E_MINTED = re.compile(r'\\end\{(minted|verbatim)\}')

#---REPLACEMENTS-----
H_TR = r'<tr><td>\1</td></tr>'
H_TD = r' </td><td> '
H_TABLE = r'<table>'
H_TABLE_END = r'</table>'

B_CODE = r'<pre><code><br>'
E_CODE = r'</code></pre>'

env_dic = {"tabular"    :{"env":TABULAR,
                          "replace":{"rows"     :[T_ROW,H_TR],
                                     "columns"  :[T_DIV,H_TD],
                                     "table"    :[T_BEGIN,H_TABLE],
                                     "/table"   :[T_END,H_TABLE_END]
                                         }
                         },
            "minted/verb":{"env":MINTED,
                           "replace":{"begin"   :[B_MINTED,B_CODE],
                                      "end"     :[E_MINTED,E_CODE]
                                      }
                            }
            }
            

def process_file(fname,root_reveal="../../www"):
    presB = open(fname).read()
    presB = process_environments(presB,env_dic).split("\n")
    presR = open(fname[:-4]+".html","w")
    docstart = False
    presR.write(addHeader(root_reveal))
    for line in presB:
        if(docstart):
            presR.write(process_line(line,repl_dict)+"\n")
        else:
            docstart = BEGIN_DOC.match(line) != None
    presR.write(addFooter(root_reveal))
    presR.close()
    

def process_line(textB,repl_dic):
    html_line = textB
    for pat,rep in repl_dic.iteritems():
        print "REPLACING:",html_line, pat,rep[1],rep[0].pattern,rep[0].search(textB)
        if rep[0].search(textB):
            html_line = rep[0].sub(rep[1],html_line)
    return html_line


def process_environments(text, env_dic):
    html_env = text
    for e_rep,e_data in env_dic.iteritems():
        e_groups = e_data["env"].search(html_env)
        if e_groups:
            html_env = e_data["env"].sub(lambda m:process_block(m.group(0),e_data["replace"]),html_env)
    return html_env


def process_block(text,exp_dic):
    return "\n".join([process_line(line,exp_dic) for line in text.split("\n")])


def addHeader(reveal_root="../../www", title="slide"):
    return '''<!doctype html>
<html lang="en">    
<head>
    <meta charset="utf-8">
    <title>%s</title>
    <link rel="stylesheet" href="%s/css/reveal.min.css">
    <link rel="stylesheet" href="%s/css/theme/moon.css" id="theme"> 
    <!-- For syntax highlighting -->
    <link rel="stylesheet" href="%slib/css/zenburn.css">
    <!--Add support for earlier versions of Internet Explorer -->
    <!--[if lt IE 9]>
    <script src="%s/lib/js/html5shiv.js"></script>
    <![endif]-->
</head>
 
<body>
    <!-- Wrap the entire slide show in a div using the "reveal" class. -->
    <div class="reveal">
        <!-- Wrap all slides in a single "slides" class -->
        <div class="slides"> '''%(title,reveal_root,reveal_root,reveal_root,reveal_root)
        
    
def addFooter(reveal_root="../../www"):
    return '''
                </div>
    </div>
    <script src="%s/lib/js/head.min.js"></script>
    <script src="%s/js/reveal.min.js"></script>
 
    <script>
        // Required, even if empty.
        Reveal.initialize({ 
        math: {
        mathjax: 'http://cdn.mathjax.org/mathjax/latest/MathJax.js',
        config: 'TeX-AMS_HTML-full'  // See http://docs.mathjax.org/en/latest/config-files.html
        },   
        dependencies: [
        // Cross-browser shim that fully implements classList - https://github.com/eligrey/classList.js/
        { src: '%s/lib/js/classList.js', condition: function() { return !document.body.classList; } },

        // Interpret Markdown in <section> elements
        { src: '%s/plugin/markdown/marked.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
        { src: '%s/plugin/markdown/markdown.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },

        // Syntax highlight for <code> elements
        { src: '%s/plugin/highlight/highlight.js', async: true, callback: function() { hljs.initHighlightingOnLoad(); } },

        // Zoom in and out with Alt+click
        { src: '%s/plugin/zoom-js/zoom.js', async: true, condition: function() { return !!document.body.classList; } },

        // Speaker notes
        { src: '%s/plugin/notes/notes.js', async: true, condition: function() { return !!document.body.classList; } },

        // MathJax
        { src: '%s/plugin/math/math.js', async: true }
        ]
        });
    </script>
</body>
</html>'''%(reveal_root,reveal_root,reveal_root,reveal_root,reveal_root,reveal_root,reveal_root,reveal_root,reveal_root)    

parser = argparse.ArgumentParser()
parser.add_argument("file",help="The filename of the beamer 'tex' file")
parser.add_argument("-r","--root", help="The root of the reveal css,js,lib and plugin folders")
args = parser.parse_args()

if __name__=="__main__":
    if args.root:
        root = args.root
    else:
        root = "../www"
    process_file(args.file,root,args.file[:-4])
    
        
    
    
